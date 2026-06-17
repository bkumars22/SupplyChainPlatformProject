/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.service;


import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.ForecastForm;
import com.scplatform.pcm.forecast.dto.ForecastPeriod;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.repo.PcmForecastRepository;
import com.scplatform.pcm.util.validator.Errors;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PcmForecastService {

    private static final String CFG_READONLY_FORECAST_ATTRIBUTES = "scplatform.flexAttributes.forecast.readonly";

    private final PcmConfigUtil pcmConfigUtil;
    private final ForecastFormService forecastFormService;
    private final PcmForecastRepository pcmForecastRepository;

    // Constructor-based injection (Spring auto-wires single constructor; no @Autowired needed).
    public PcmForecastService(PcmConfigUtil pcmConfigUtil,
                              ForecastFormService forecastFormService,
                              PcmForecastRepository pcmForecastRepository) {
        this.pcmConfigUtil = pcmConfigUtil;
        this.forecastFormService = forecastFormService;
        this.pcmForecastRepository = pcmForecastRepository;
    }

    public List<PcmForecast> findAllByIds(List<Long> forecastKeys) {
        return pcmForecastRepository.findAllById(forecastKeys);
    }

    public void addAttribute(PcmForecast pcmForecast, Attribute attribute) {
        setAttribute(pcmForecast, attribute.getAssociatedAttribute(), attribute.getAttrValue());
    }

    public Object getAttribute(PcmForecast pcmForecast, String associatedAttribute) {
        Object obj = null;
        if (associatedAttribute.equals("stringAttribute1")) {
            obj = pcmForecast.getStringAttribute1();
        } else if (associatedAttribute.equals("stringAttribute2")) {
            obj = pcmForecast.getStringAttribute2();
        } else if (associatedAttribute.equals("stringAttribute3")) {
            obj = pcmForecast.getStringAttribute3();
        } else if (associatedAttribute.equals("stringAttribute4")) {
            obj = pcmForecast.getStringAttribute4();
        } else if (associatedAttribute.equals("stringAttribute5")) {
            obj = pcmForecast.getStringAttribute5();
        } else if (associatedAttribute.equals("stringAttribute6")) {
            obj = pcmForecast.getStringAttribute6();
        } else if (associatedAttribute.equals("stringAttribute7")) {
            obj = pcmForecast.getStringAttribute7();
        } else if (associatedAttribute.equals("stringAttribute8")) {
            obj = pcmForecast.getStringAttribute8();
        } else if (associatedAttribute.equals("stringAttribute9")) {
            obj = pcmForecast.getStringAttribute9();
        } else if (associatedAttribute.equals("stringAttribute10")) {
            obj = pcmForecast.getStringAttribute10();
        } else if (associatedAttribute.equals("numberAttribute1")) {
            obj = pcmForecast.getNumberAttribute1();
        } else if (associatedAttribute.equals("numberAttribute2")) {
            obj = pcmForecast.getNumberAttribute2();
        } else if (associatedAttribute.equals("numberAttribute3")) {
            obj = pcmForecast.getNumberAttribute3();
        } else if (associatedAttribute.equals("numberAttribute4")) {
            obj = pcmForecast.getNumberAttribute4();
        } else if (associatedAttribute.equals("numberAttribute5")) {
            obj = pcmForecast.getNumberAttribute5();
        } else if (associatedAttribute.equals("numberAttribute6")) {
            obj = pcmForecast.getNumberAttribute6();
        } else if (associatedAttribute.equals("numberAttribute7")) {
            obj = pcmForecast.getNumberAttribute7();
        } else if (associatedAttribute.equals("numberAttribute8")) {
            obj = pcmForecast.getNumberAttribute8();
        } else if (associatedAttribute.equals("numberAttribute9")) {
            obj = pcmForecast.getNumberAttribute9();
        } else if (associatedAttribute.equals("numberAttribute10")) {
            obj = pcmForecast.getNumberAttribute10();
        }

        if (associatedAttribute.equals("floatAttribute1")) {
            obj = pcmForecast.getFloatAttribute1();
        } else if (associatedAttribute.equals("floatAttribute2")) {
            obj = pcmForecast.getFloatAttribute2();
        } else if (associatedAttribute.equals("floatAttribute3")) {
            obj = pcmForecast.getFloatAttribute3();
        } else if (associatedAttribute.equals("floatAttribute4")) {
            obj = pcmForecast.getFloatAttribute4();
        } else if (associatedAttribute.equals("floatAttribute5")) {
            obj = pcmForecast.getFloatAttribute5();
        } else if (associatedAttribute.equals("floatAttribute6")) {
            obj = pcmForecast.getFloatAttribute6();
        } else if (associatedAttribute.equals("floatAttribute7")) {
            obj = pcmForecast.getFloatAttribute7();
        } else if (associatedAttribute.equals("floatAttribute8")) {
            obj = pcmForecast.getFloatAttribute8();
        } else if (associatedAttribute.equals("floatAttribute9")) {
            obj = pcmForecast.getFloatAttribute9();
        } else if (associatedAttribute.equals("floatAttribute10")) {
            obj = pcmForecast.getFloatAttribute10();
        }

        if (associatedAttribute.equals("dateAttribute1")) {
            obj = pcmForecast.getDateAttribute1();
        } else if (associatedAttribute.equals("dateAttribute2")) {
            obj = pcmForecast.getDateAttribute2();
        } else if (associatedAttribute.equals("dateAttribute3")) {
            obj = pcmForecast.getDateAttribute3();
        } else if (associatedAttribute.equals("dateAttribute4")) {
            obj = pcmForecast.getDateAttribute4();
        } else if (associatedAttribute.equals("dateAttribute5")) {
            obj = pcmForecast.getDateAttribute5();
        } else if (associatedAttribute.equals("dateAttribute6")) {
            obj = pcmForecast.getDateAttribute6();
        } else if (associatedAttribute.equals("dateAttribute7")) {
            obj = pcmForecast.getDateAttribute7();
        } else if (associatedAttribute.equals("dateAttribute8")) {
            obj = pcmForecast.getDateAttribute8();
        } else if (associatedAttribute.equals("dateAttribute9")) {
            obj = pcmForecast.getDateAttribute9();
        } else if (associatedAttribute.equals("dateAttribute10")) {
            obj = pcmForecast.getDateAttribute10();
        }
        return obj;
    }

    public void setAttribute(PcmForecast pcmForecast, String key, Object value) {
        if (isReadonly(key)) {
            return;
        }
        if (key.equals("stringAttribute1")) {
            pcmForecast.setStringAttribute1((String) value);
        } else if (key.equals("stringAttribute2")) {
            pcmForecast.setStringAttribute2((String) value);
        } else if (key.equals("stringAttribute3")) {
            pcmForecast.setStringAttribute3((String) value);
        } else if (key.equals("stringAttribute4")) {
            pcmForecast.setStringAttribute4((String) value);
        } else if (key.equals("stringAttribute5")) {
            pcmForecast.setStringAttribute5((String) value);
        } else if (key.equals("stringAttribute6")) {
            pcmForecast.setStringAttribute6((String) value);
        } else if (key.equals("stringAttribute7")) {
            pcmForecast.setStringAttribute7((String) value);
        } else if (key.equals("stringAttribute8")) {
            pcmForecast.setStringAttribute8((String) value);
        } else if (key.equals("stringAttribute9")) {
            pcmForecast.setStringAttribute9((String) value);
        } else if (key.equals("stringAttribute10")) {
            pcmForecast.setStringAttribute10((String) value);
        }

        if (key.equals("numberAttribute1")) {
            pcmForecast.setNumberAttribute1((Integer) value);
        } else if (key.equals("numberAttribute2")) {
            pcmForecast.setNumberAttribute2((Integer) value);
        } else if (key.equals("numberAttribute3")) {
            pcmForecast.setNumberAttribute3((Integer) value);
        } else if (key.equals("numberAttribute4")) {
            pcmForecast.setNumberAttribute4((Integer) value);
        } else if (key.equals("numberAttribute5")) {
            pcmForecast.setNumberAttribute5((Integer) value);
        } else if (key.equals("numberAttribute6")) {
            pcmForecast.setNumberAttribute6((Integer) value);
        } else if (key.equals("numberAttribute7")) {
            pcmForecast.setNumberAttribute7((Integer) value);
        } else if (key.equals("numberAttribute8")) {
            pcmForecast.setNumberAttribute8((Integer) value);
        } else if (key.equals("numberAttribute9")) {
            pcmForecast.setNumberAttribute9((Integer) value);
        } else if (key.equals("numberAttribute10")) {
            pcmForecast.setNumberAttribute10((Integer) value);
        }

        if (key.equals("floatAttribute1")) {
            pcmForecast.setFloatAttribute1((BigDecimal) value);
        } else if (key.equals("floatAttribute2")) {
            pcmForecast.setFloatAttribute2((BigDecimal) value);
        } else if (key.equals("floatAttribute3")) {
            pcmForecast.setFloatAttribute3((BigDecimal) value);
        } else if (key.equals("floatAttribute4")) {
            pcmForecast.setFloatAttribute4((BigDecimal) value);
        } else if (key.equals("floatAttribute5")) {
            pcmForecast.setFloatAttribute5((BigDecimal) value);
        } else if (key.equals("floatAttribute6")) {
            pcmForecast.setFloatAttribute6((BigDecimal) value);
        } else if (key.equals("floatAttribute7")) {
            pcmForecast.setFloatAttribute7((BigDecimal) value);
        } else if (key.equals("floatAttribute8")) {
            pcmForecast.setFloatAttribute8((BigDecimal) value);
        } else if (key.equals("floatAttribute9")) {
            pcmForecast.setFloatAttribute9((BigDecimal) value);
        } else if (key.equals("floatAttribute10")) {
            pcmForecast.setFloatAttribute10((BigDecimal) value);
        }

        if (key.equals("dateAttribute1")) {
            pcmForecast.setDateAttribute1((Date) value);
        } else if (key.equals("dateAttribute2")) {
            pcmForecast.setDateAttribute2((Date) value);
        } else if (key.equals("dateAttribute3")) {
            pcmForecast.setDateAttribute3((Date) value);
        } else if (key.equals("dateAttribute4")) {
            pcmForecast.setDateAttribute4((Date) value);
        } else if (key.equals("dateAttribute5")) {
            pcmForecast.setDateAttribute5((Date) value);
        } else if (key.equals("dateAttribute6")) {
            pcmForecast.setDateAttribute6((Date) value);
        } else if (key.equals("dateAttribute7")) {
            pcmForecast.setDateAttribute7((Date) value);
        } else if (key.equals("dateAttribute8")) {
            pcmForecast.setDateAttribute8((Date) value);
        } else if (key.equals("dateAttribute9")) {
            pcmForecast.setDateAttribute9((Date) value);
        } else if (key.equals("dateAttribute10")) {
            pcmForecast.setDateAttribute10((Date) value);
        }
    }

    private boolean isReadonly(String key) {
        List<String> readonly = pcmConfigUtil.getList(CFG_READONLY_FORECAST_ATTRIBUTES);
        return readonly != null && readonly.contains(key);
    }

    public void setReadonlyAttribute(PcmForecast pcmForecast, String key, Object value) {
        if (key.equals("stringAttribute1")) {
            pcmForecast.setStringAttribute1((String) value);
        } else if (key.equals("stringAttribute2")) {
            pcmForecast.setStringAttribute2((String) value);
        } else if (key.equals("stringAttribute3")) {
            pcmForecast.setStringAttribute3((String) value);
        } else if (key.equals("stringAttribute4")) {
            pcmForecast.setStringAttribute4((String) value);
        } else if (key.equals("stringAttribute5")) {
            pcmForecast.setStringAttribute5((String) value);
        } else if (key.equals("stringAttribute6")) {
            pcmForecast.setStringAttribute6((String) value);
        } else if (key.equals("stringAttribute7")) {
            pcmForecast.setStringAttribute7((String) value);
        } else if (key.equals("stringAttribute8")) {
            pcmForecast.setStringAttribute8((String) value);
        } else if (key.equals("stringAttribute9")) {
            pcmForecast.setStringAttribute9((String) value);
        } else if (key.equals("stringAttribute10")) {
            pcmForecast.setStringAttribute10((String) value);
        }

        if (key.equals("numberAttribute1")) {
            pcmForecast.setNumberAttribute1((Integer) value);
        } else if (key.equals("numberAttribute2")) {
            pcmForecast.setNumberAttribute2((Integer) value);
        } else if (key.equals("numberAttribute3")) {
            pcmForecast.setNumberAttribute3((Integer) value);
        } else if (key.equals("numberAttribute4")) {
            pcmForecast.setNumberAttribute4((Integer) value);
        } else if (key.equals("numberAttribute5")) {
            pcmForecast.setNumberAttribute5((Integer) value);
        } else if (key.equals("numberAttribute6")) {
            pcmForecast.setNumberAttribute6((Integer) value);
        } else if (key.equals("numberAttribute7")) {
            pcmForecast.setNumberAttribute7((Integer) value);
        } else if (key.equals("numberAttribute8")) {
            pcmForecast.setNumberAttribute8((Integer) value);
        } else if (key.equals("numberAttribute9")) {
            pcmForecast.setNumberAttribute9((Integer) value);
        } else if (key.equals("numberAttribute10")) {
            pcmForecast.setNumberAttribute10((Integer) value);
        }

        if (key.equals("floatAttribute1")) {
            pcmForecast.setFloatAttribute1((BigDecimal) value);
        } else if (key.equals("floatAttribute2")) {
            pcmForecast.setFloatAttribute2((BigDecimal) value);
        } else if (key.equals("floatAttribute3")) {
            pcmForecast.setFloatAttribute3((BigDecimal) value);
        } else if (key.equals("floatAttribute4")) {
            pcmForecast.setFloatAttribute4((BigDecimal) value);
        } else if (key.equals("floatAttribute5")) {
            pcmForecast.setFloatAttribute5((BigDecimal) value);
        } else if (key.equals("floatAttribute6")) {
            pcmForecast.setFloatAttribute6((BigDecimal) value);
        } else if (key.equals("floatAttribute7")) {
            pcmForecast.setFloatAttribute7((BigDecimal) value);
        } else if (key.equals("floatAttribute8")) {
            pcmForecast.setFloatAttribute8((BigDecimal) value);
        } else if (key.equals("floatAttribute9")) {
            pcmForecast.setFloatAttribute9((BigDecimal) value);
        } else if (key.equals("floatAttribute10")) {
            pcmForecast.setFloatAttribute10((BigDecimal) value);
        }

        if (key.equals("dateAttribute1")) {
            pcmForecast.setDateAttribute1((Date) value);
        } else if (key.equals("dateAttribute2")) {
            pcmForecast.setDateAttribute2((Date) value);
        } else if (key.equals("dateAttribute3")) {
            pcmForecast.setDateAttribute3((Date) value);
        } else if (key.equals("dateAttribute4")) {
            pcmForecast.setDateAttribute4((Date) value);
        } else if (key.equals("dateAttribute5")) {
            pcmForecast.setDateAttribute5((Date) value);
        } else if (key.equals("dateAttribute6")) {
            pcmForecast.setDateAttribute6((Date) value);
        } else if (key.equals("dateAttribute7")) {
            pcmForecast.setDateAttribute7((Date) value);
        } else if (key.equals("dateAttribute8")) {
            pcmForecast.setDateAttribute8((Date) value);
        } else if (key.equals("dateAttribute9")) {
            pcmForecast.setDateAttribute9((Date) value);
        } else if (key.equals("dateAttribute10")) {
            pcmForecast.setDateAttribute10((Date) value);
        }
    }
    public int getCurrentPeriodIndex(List<ForecastPeriod> periods)
    {
        for (int idx=0; idx < periods.size(); idx++)
        {
            if (periods.get(idx).getState() == ForecastTimeline.ForecastPeriodState.CURRENT)
            {
                return idx;
            }
        }
        return -1;
    }

    public ForecastPeriod getCurrentPeriod(List<ForecastPeriod> periods)
    {
        for (ForecastPeriod fp: periods)
        {
            if (fp.getState() == ForecastTimeline.ForecastPeriodState.CURRENT)
            {
                return fp;
            }
        }
        return null;
    }

    public int getFuturePeriodCount(List<ForecastPeriod> periods)
    {
        int cnt = 0;
        for (ForecastPeriod fp: periods)
        {
            if (fp.getState() == ForecastTimeline.ForecastPeriodState.FUTURE)
            {
                cnt++;
            }
        }
        return cnt;
    }


    public ForecastPeriod getPeriod(List<ForecastPeriod> periods, Date startDate)
    {
        for (ForecastPeriod fp: periods)
        {
            if (DateUtils.isSameDay(fp.getStartDate(), startDate))
            {
                return fp;
            }
        }
        return null;
    }

    public int getPastPeriodCount(List<ForecastPeriod> periods)
    {
        int cnt = 0;
        for (ForecastPeriod fp: periods)
        {
            if (fp.getState() == ForecastTimeline.ForecastPeriodState.PAST)
            {
                cnt++;
            }
        }
        return cnt;
    }

    public Errors validateForecastForm(
            ForecastForm form) {
        if (form == null) {
            throw new IllegalArgumentException("ForecastForm must not be null");
        }
        Errors errors = new Errors();
        return forecastFormService.validate(form, errors);
    }
}
