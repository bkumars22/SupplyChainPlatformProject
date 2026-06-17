/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FgPlatformListIntializer implements SearchParameterInitializer {

    private final PcmConfigUtil pcmConfigUtil;

    @Override
    public boolean initializeParameter(SearchParameter parameter, Map context) {
        if (parameter instanceof SearchParameterSelect) {
            SearchParameterSelect list = (SearchParameterSelect) parameter;
            List<String> platformList = pcmConfigUtil.getList("pcm.functional.platform.types", new ArrayList<>());

            for (String platform : platformList) {
                list.addSelectValue(platform, platform);
            }

            return true;
        }
        return false;
    }

    @Override
    public void setInitialData(String data) {
    }
}

