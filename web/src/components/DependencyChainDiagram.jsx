/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 *
 * scip_master_plan Phase 1, Step 4: "Show the actual chain: This Supplier
 * <- Tier-2 Source <- Tier-3 Source, with each node colored by its own
 * direct risk level, and sole-source edges visually distinguished (red
 * dashed line) from redundant/substitutable ones (gray solid line)."
 */
import { useNavigate } from 'react-router-dom';

const COL_WIDTH = 200;
const COL_GAP   = 130;
const NODE_H    = 56;
const NODE_GAP  = 14;

function riskColor(compositeScore) {
  if (compositeScore == null) return '#9ca3af'; // unknown -- gray
  return compositeScore >= 80 ? '#16a34a' : compositeScore >= 60 ? '#d97706' : '#c62828';
}

/**
 * Walks the dependency graph upstream from `rootId`, mirroring the same
 * hop-by-hop, visit-once BFS SupplierRiskCascadeService.computeCascade
 * uses server-side (see that class for why: it's what makes circular
 * dependencies terminate instead of drawing forever).
 */
function buildLevels(rootId, edges, maxHops) {
  const upstreamOf = {};
  for (const e of edges) {
    (upstreamOf[e.dependentSupplierId] ??= []).push(e);
  }

  const levels = [[{ id: rootId, edge: null }]];
  const visited = new Set([rootId]);
  let current = [rootId];

  for (let hop = 1; hop <= maxHops && current.length; hop++) {
    const next = [];
    const nodes = [];
    for (const id of current) {
      for (const edge of upstreamOf[id] || []) {
        if (visited.has(edge.upstreamSupplierId)) continue;
        visited.add(edge.upstreamSupplierId);
        nodes.push({ id: edge.upstreamSupplierId, edge });
        next.push(edge.upstreamSupplierId);
      }
    }
    if (nodes.length === 0) break;
    levels.push(nodes);
    current = next;
  }
  return levels;
}

export default function DependencyChainDiagram({ rootId, edges, riskById, maxHops = 3 }) {
  const navigate = useNavigate();
  const levels = buildLevels(rootId, edges, maxHops);

  if (levels.length === 1) {
    return null; // no upstream dependencies at all -- nothing to draw
  }

  const width  = levels.length * COL_WIDTH + (levels.length - 1) * COL_GAP;
  const height = Math.max(...levels.map(l => l.length)) * (NODE_H + NODE_GAP) - NODE_GAP;

  const posOf = {}; // id -> {x, y, level}
  levels.forEach((level, li) => {
    level.forEach((node, ni) => {
      posOf[node.id] = { x: li * (COL_WIDTH + COL_GAP), y: ni * (NODE_H + NODE_GAP), level: li };
    });
  });

  // One edge per non-root node, connecting it to the node it feeds
  // (the one hop closer to root that pulled it into the graph).
  const lines = [];
  for (let li = 1; li < levels.length; li++) {
    for (const node of levels[li]) {
      const from = posOf[node.edge.dependentSupplierId]; // closer to root
      const to   = posOf[node.id];                        // this node
      lines.push({ from, to, edge: node.edge });
    }
  }

  return (
    <div>
      <h3 style={{ marginBottom: 10 }}>Dependency Chain</h3>
      <div style={{ overflowX: 'auto', background: '#fff', borderRadius: 10, padding: 20, boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
        <div style={{ position: 'relative', width, height, minWidth: width }}>
          <svg width={width} height={height} style={{ position: 'absolute', top: 0, left: 0, overflow: 'visible' }}>
            <defs>
              <marker id="dep-arrow-solid" markerWidth="8" markerHeight="8" refX="7" refY="4" orient="auto">
                <path d="M0,0 L8,4 L0,8 Z" fill="#9ca3af" />
              </marker>
              <marker id="dep-arrow-sole" markerWidth="8" markerHeight="8" refX="7" refY="4" orient="auto">
                <path d="M0,0 L8,4 L0,8 Z" fill="#c62828" />
              </marker>
            </defs>
            {lines.map((l, i) => {
              const x1 = l.to.x;                    // left edge of upstream node
              const y1 = l.to.y + NODE_H / 2;
              const x2 = l.from.x + COL_WIDTH;       // right edge of dependent node
              const y2 = l.from.y + NODE_H / 2;
              const sole = l.edge.isSoleSource;
              return (
                <g key={i}>
                  <line
                    x1={x1} y1={y1} x2={x2} y2={y2}
                    stroke={sole ? '#c62828' : '#9ca3af'}
                    strokeWidth={sole ? 2.5 : 1.5}
                    strokeDasharray={sole ? '6,4' : undefined}
                    markerEnd={`url(#${sole ? 'dep-arrow-sole' : 'dep-arrow-solid'})`}
                  />
                  {l.edge.componentOrMaterial && (
                    <text
                      x={(x1 + x2) / 2} y={(y1 + y2) / 2 - 6}
                      textAnchor="middle" fontSize="10"
                      fill={sole ? '#c62828' : '#6b7280'} fontWeight={sole ? 700 : 500}
                      stroke="#fff" strokeWidth="3" paintOrder="stroke"
                    >
                      {l.edge.componentOrMaterial}
                    </text>
                  )}
                </g>
              );
            })}
          </svg>

          {levels.map((level, li) =>
            level.map((node, ni) => {
              const pos = posOf[node.id];
              const risk = riskById[node.id];
              const isRoot = li === 0;
              return (
                <div
                  key={node.id}
                  onClick={() => !isRoot && navigate('/suppliers/' + node.id)}
                  title={risk ? `Composite score: ${risk.compositeScore?.toFixed(1)}` : undefined}
                  style={{
                    position: 'absolute', left: pos.x, top: pos.y, width: COL_WIDTH, height: NODE_H,
                    background: '#fff', border: `2px solid ${riskColor(risk?.compositeScore)}`,
                    borderRadius: 8, padding: '6px 10px', display: 'flex', flexDirection: 'column',
                    justifyContent: 'center', cursor: isRoot ? 'default' : 'pointer',
                    boxShadow: isRoot ? '0 0 0 3px rgba(30,42,59,0.12)' : 'none',
                  }}
                >
                  <div style={{ fontSize: 12, fontWeight: 700, color: '#1e293b', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {risk?.supplierName || node.id}
                  </div>
                  <div style={{ fontSize: 10, color: riskColor(risk?.compositeScore), fontWeight: 700, marginTop: 2 }}>
                    {isRoot ? 'This supplier' : `Hop ${li}`} · {risk?.compositeScore != null ? risk.compositeScore.toFixed(0) : '—'}/100
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>
      <div style={{ display: 'flex', gap: 16, marginTop: 10, fontSize: 11, color: '#6b7280' }}>
        <span><svg width="20" height="10"><line x1="0" y1="5" x2="20" y2="5" stroke="#c62828" strokeWidth="2.5" strokeDasharray="6,4" /></svg> Sole source (no backup)</span>
        <span><svg width="20" height="10"><line x1="0" y1="5" x2="20" y2="5" stroke="#9ca3af" strokeWidth="1.5" /></svg> Redundant / substitutable</span>
      </div>
    </div>
  );
}
