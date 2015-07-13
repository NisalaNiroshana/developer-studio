/*
 * Copyright (c) WSO2, Inc. (http://wso2.com)
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

package org.wso2.developerstudio.eclipse.gmf.esb.internal.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.config.xml.MessageStoreSerializer;
import org.eclipse.emf.common.util.EList;
import org.wso2.developerstudio.eclipse.gmf.esb.MessageStore;
import org.wso2.developerstudio.eclipse.gmf.esb.MessageStoreParameter;
import org.wso2.developerstudio.eclipse.gmf.esb.MessageStoreType;
import org.apache.synapse.message.store.impl.memory.InMemoryStore;

public class MessageStoreTransformer {

	private static final String STORE_RABBITMQ_VIRTUAL_HOST = "store.rabbitmq.virtual.host";
	private static final String STORE_RABBITMQ_PASSWORD = "store.rabbitmq.password";
	private static final String STORE_RABBITMQ_USERNAME = "store.rabbitmq.username";
	private static final String STORE_RABBITMQ_ROUTE_KEY = "store.rabbitmq.route.key";
	private static final String STORE_RABBITMQ_EXCHANGE_NAME = "store.rabbitmq.exchange.name";
	private static final String STORE_RABBITMQ_QUEUE_NAME = "store.rabbitmq.queue.name";
	private static final String STORE_RABBITMQ_HOST_PORT = "store.rabbitmq.host.port";
	private static final String STORE_RABBITMQ_HOST_NAME = "store.rabbitmq.host.name";

	private static final String RABBITMQ_MS_FQN = "org.apache.synapse.message.store.impl.rabbitmq.RabbitMQStore";
	
	public static OMElement createMessageStore(MessageStore model) throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		// Fixing TOOLS-2026.
		//String className = "org.apache.synapse.message.store.InMemoryMessageStore";
		//String className = "org.apache.synapse.message.store.impl.memory.InMemoryStore";
		String className = null;

		org.apache.synapse.message.store.MessageStore messageStore = new InMemoryStore();
		messageStore.setName(model.getStoreName());

		if (model.getStoreType() == MessageStoreType.CUSTOM) {
			className = model.getProviderClass();
			EList<MessageStoreParameter> parameters2 = model.getParameters();
			for (MessageStoreParameter param : parameters2) {
				if (!StringUtils.isBlank(param.getParameterName())
						&& !StringUtils.isBlank(param.getParameterValue())) {
					parameters.put(param.getParameterName(), param.getParameterValue());
				}
			}
		} else if (model.getStoreType() == MessageStoreType.JMS) {
			// Fixing TOOLS-2026.
			//className = "org.wso2.carbon.message.store.persistence.jms.JMSMessageStore";
			className = "org.apache.synapse.message.store.impl.jms.JmsStore";
			if (!StringUtils.isBlank(model.getInitialContextFactory())) {
				parameters.put("java.naming.factory.initial",
						model.getInitialContextFactory());
			}
			if (!StringUtils.isBlank(model.getProviderURL())) {
				parameters.put("java.naming.provider.url", model.getProviderURL());
			}
			if (!StringUtils.isBlank(model.getJndiQueueName())) {
				parameters.put("store.jms.destination", model.getJndiQueueName());
			}
			if (!StringUtils.isBlank(model.getConnectionFactory())) {
				parameters.put("store.jms.connection.factory",
						model.getConnectionFactory());
			}
			if (!StringUtils.isBlank(model.getUserName())) {
				parameters.put("store.jms.username", model.getUserName());
			}
			if (!StringUtils.isBlank(model.getPassword())) {
				parameters.put("store.jms.password", model.getPassword());
			}
			parameters.put("store.jms.JMSSpecVersion", model.getJmsSpecVersion().getLiteral());
			parameters.put("store.jms.cache.connection",
					((Boolean) model.isEnableCaching()).toString());
			/*parameters.put("store.jms.ConsumerReceiveTimeOut",
					((Long) model.getTimeout()).toString());*/

			/*
			 * Any additional parameters not listed above will handle here
			 * Fixing TOOLS-2286
			 */
			EList<MessageStoreParameter> additionalParameters = model.getParameters();
			for (MessageStoreParameter additionalParameter : additionalParameters) {
				if (!StringUtils.isBlank(additionalParameter.getParameterName())
						&& !StringUtils.isBlank(additionalParameter.getParameterValue())) {
					parameters.put(additionalParameter.getParameterName(),
							additionalParameter.getParameterValue());
				}
			}
		} else if (model.getStoreType() == MessageStoreType.RABBITMQ) {
			className = RABBITMQ_MS_FQN;
			if (StringUtils.isNotBlank(model.getRabbitMQServerHostName())) {
				parameters.put(STORE_RABBITMQ_HOST_NAME, model.getRabbitMQServerHostName());
			}
			if (StringUtils.isNotBlank(model.getRabbitMQServerHostPort())) {
				parameters.put(STORE_RABBITMQ_HOST_PORT, model.getRabbitMQServerHostPort());
			}
			if (StringUtils.isNotBlank(model.getRabbitMQQueueName())) {
				parameters.put(STORE_RABBITMQ_QUEUE_NAME, model.getRabbitMQQueueName());
			}
			if (StringUtils.isNotBlank(model.getRabbitMQExchangeName())) {
				parameters.put(STORE_RABBITMQ_EXCHANGE_NAME, model.getRabbitMQExchangeName());
			}
			if (StringUtils.isNotBlank(model.getRoutingKey())) {
				parameters.put(STORE_RABBITMQ_ROUTE_KEY, model.getRoutingKey());
			}
			if (StringUtils.isNotBlank(model.getRabbitMQUserName())) {
				parameters.put(STORE_RABBITMQ_USERNAME, model.getRabbitMQUserName());
			}
			if (StringUtils.isNotBlank(model.getRabbitMQPassword())) {
				parameters.put(STORE_RABBITMQ_PASSWORD, model.getRabbitMQPassword());
			}
			if (StringUtils.isNotBlank(model.getVirtualHost())) {
				parameters.put(STORE_RABBITMQ_VIRTUAL_HOST, model.getVirtualHost());
			}
		}
		
		messageStore.setParameters(parameters);

		OMElement messageStoreElement = MessageStoreSerializer.serializeMessageStore(null,
				messageStore);
		OMAttribute classAttr = messageStoreElement.getAttribute(new QName("class"));
		if (classAttr != null) {
			classAttr.setAttributeValue(className);
		} else if (!StringUtils.isBlank(className)) {
			messageStoreElement.addAttribute("class", className, null);
		} else {
			/*
			 *  Class attribute is optional for In-Memory Store. If class attribute is 
			 *  not defined it will be considered as an In-Memory Store. 
			 */
			//messageStoreElement.addAttribute("class", className, null);
		}

		return messageStoreElement;

	}

}
