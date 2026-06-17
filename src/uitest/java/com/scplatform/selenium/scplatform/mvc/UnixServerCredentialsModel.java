/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.mvc;

import com.test.selenium.common.modelViewController.model.Model;

public class UnixServerCredentialsModel extends Model {

    public static class ServerCredentials {
        public String host     = System.getenv().getOrDefault("UITEST_SSH_HOST", "dev3077.dev.scplatform.local");
        public String username = System.getenv().getOrDefault("UITEST_SSH_USER", "kswamy");
        public String password = System.getenv("UITEST_SSH_PASSWORD");   // must be set via env var
        public String remoteDir = "/scplatform/app/scplatform/config";
        public String fileName  = "pcm-config.properties";
    }

}
