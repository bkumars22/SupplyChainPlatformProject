/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;


import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import com.scplatform.pcm.util.stateMachine.StateMachine;
import com.scplatform.pcm.util.stateMachine.StateMachineFactory;
import com.scplatform.pcm.util.stateMachine.StateMachineState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class SMStateInitializer implements SearchParameterInitializer
{
	protected String defaultType;
    private final StateMachineFactory stateMachineFactory;

	public boolean initializeParameter(SearchParameter parameter, Map context)
	{
		if (parameter instanceof SearchParameterSelect)
		{
			SearchParameterSelect list = (SearchParameterSelect)parameter;
			String type = (String)context.get(SearchParameterInitializer.REQUEST_TYPE);
			if (type == null)
			{
				type = defaultType;
			}
			StateMachine sm = stateMachineFactory.getStateMachine(type);
			if (sm == null)
			{
				return false;
			}
			Iterator stateItr = sm.getAllStates().iterator();
			while (stateItr.hasNext())
			{
				StateMachineState state = (StateMachineState)stateItr.next();
				list.addSelectValue(state.getLabel(), state.getName());
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public void setInitialData(String data)
	{
		defaultType = data;
	}
}
