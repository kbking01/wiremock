/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.junit;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.github.tomakehurst.wiremock.AbstractWireMockServer;
import com.github.tomakehurst.wiremock.JettyWireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class WireMockStaticRule implements MethodRule {

	private final AbstractWireMockServer wireMockServer;
	
	public WireMockStaticRule(int port) {
		wireMockServer = new JettyWireMockServer(port);
		wireMockServer.start();
		WireMock.configureFor("localhost", port);
	}
	
	public WireMockStaticRule() {
		this(AbstractWireMockServer.DEFAULT_PORT);
	}
	
	public void stopServer() {
		wireMockServer.stop();
	}

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
				WireMock.reset();
			}
			
		};
	}
}