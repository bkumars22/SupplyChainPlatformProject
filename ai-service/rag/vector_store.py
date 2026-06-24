"""pgvector-backed document store for SCIP RAG.

Stores supplier reports, risk analyses, and disruption events so
CEO queries like "which supplier is high risk?" retrieve real data.
"""

from __future__ import annotations

import json
import logging
import os
from typing import Any

import psycopg2
import psycopg2.extras

logger = logging.getLogger("rag.vector_store")

_DDL = """
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS rag_documents (
    id          BIGSERIAL PRIMARY KEY,
    content     TEXT        NOT NULL,
    embedding   vector(384),
    metadata    JSONB       DEFAULT '{}',
    source_type VARCHAR(50),
    source_id   VARCHAR(255),
    project_id  VARCHAR(255),
    created_at  TIMESTAMP   DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS rag_documents_project_idx
    ON rag_documents (project_id, source_type);

CREATE INDEX IF NOT EXISTS rag_documents_embedding_idx
    ON rag_documents USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 50);
"""

_DB_URL = os.getenv("DATABASE_URL", "")


def _conn():
    if not _DB_URL:
        raise RuntimeError("DATABASE_URL not set")
    try:
        from pgvector.psycopg2 import register_vector
        c = psycopg2.connect(_DB_URL)
        register_vector(c)
        return c
    except ImportError:
        return psycopg2.connect(_DB_URL)


def ensure_schema() -> bool:
    try:
        with _conn() as c:
            with c.cursor() as cur:
                cur.execute(_DDL)
        logger.info("SCIP pgvector schema ready")
        return True
    except Exception as exc:
        logger.warning("Schema setup failed: %s", exc)
        return False


def upsert(
    content: str,
    embedding: list[float],
    source_type: str,
    source_id: str,
    project_id: str = "SCIP",
    metadata: dict[str, Any] | None = None,
) -> int | None:
    vec_str = "[" + ",".join(f"{v:.6f}" for v in embedding) + "]"
    sql = """
        INSERT INTO rag_documents
               (content, embedding, metadata, source_type, source_id, project_id)
        VALUES (%s, %s::vector, %s::jsonb, %s, %s, %s)
        ON CONFLICT DO NOTHING
        RETURNING id
    """
    try:
        with _conn() as c:
            with c.cursor() as cur:
                cur.execute(sql, (content, vec_str, json.dumps(metadata or {}), source_type, source_id, project_id))
                row = cur.fetchone()
                return row[0] if row else None
    except Exception as exc:
        logger.warning("upsert failed: %s", exc)
        return None


def search(
    query_embedding: list[float],
    project_id: str = "SCIP",
    top_k: int = 5,
    source_type: str | None = None,
    min_similarity: float = 0.25,
) -> list[dict[str, Any]]:
    vec_str = "[" + ",".join(f"{v:.6f}" for v in query_embedding) + "]"
    type_clause = "AND source_type = %s" if source_type else ""
    params: list[Any] = [vec_str, project_id]
    if source_type:
        params.append(source_type)
    params += [vec_str, top_k]

    sql = f"""
        SELECT content, metadata, source_type,
               1 - (embedding <=> %s::vector) AS similarity
        FROM   rag_documents
        WHERE  project_id = %s {type_clause}
               AND embedding IS NOT NULL
        ORDER  BY embedding <=> %s::vector
        LIMIT  %s
    """
    try:
        with _conn() as c:
            with c.cursor(cursor_factory=psycopg2.extras.RealDictCursor) as cur:
                cur.execute(sql, params)
                rows = cur.fetchall()
        return [
            {
                "content": r["content"],
                "metadata": r["metadata"] if isinstance(r["metadata"], dict) else json.loads(r["metadata"] or "{}"),
                "source_type": r["source_type"],
                "similarity": round(float(r["similarity"]), 4),
            }
            for r in rows
            if float(r["similarity"]) >= min_similarity
        ]
    except Exception as exc:
        logger.warning("search failed: %s", exc)
        return []
