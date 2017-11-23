/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.zk.spring.annotations;

import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.zkoss.spring.bean.ZkComponentFactoryBean;
import org.zkoss.spring.config.ZkSpringBeanBindingComposer;
import org.zkoss.spring.security.config.ZkBeanIds;
import org.zkoss.spring.web.context.request.*;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.Event;

import java.util.HashMap;
import java.util.Map;

public class ZkSpringConfigRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry reg) {
		//register ZK scopes
		final BeanDefinitionBuilder builder =
				BeanDefinitionBuilder.rootBeanDefinition(CustomScopeConfigurer.class);
		final Map scopes = new HashMap();
		scopes.put("application", new ApplicationScope());
		scopes.put("desktop", new DesktopScope());
		scopes.put("page", new PageScope());
		scopes.put("idspace", new IdSpaceScope());
		scopes.put("execution", new ExecutionScope());
		builder.addPropertyValue("scopes", scopes);
		reg.registerBeanDefinition(ZkBeanIds.ZK_SCOPE_CONFIG, builder.getBeanDefinition());

		//register ZK implicit object factory bean
		registerImplicitObjects(reg);

		//register singleton ZkSpringBeanBindingComposer (used to bind ZK component as Spring bean),
		//see ZkDesktopReuseUiFactory#applyZkSpringBeanBindingComposer(PageDefinition pd)
		final BeanDefinitionBuilder bbuilder =
				BeanDefinitionBuilder.rootBeanDefinition(ZkSpringBeanBindingComposer.class);
		reg.registerBeanDefinition(ZkBeanIds.ZK_BINDING_COMPOSER, bbuilder.getBeanDefinition());

		//register PropertyEditor for ZkComponentProxyFactoryBean#type
		final BeanDefinitionBuilder xbuilder =
				BeanDefinitionBuilder.rootBeanDefinition(CustomEditorConfigurer.class);
		final Map editors = new HashMap();
		editors.put("org.zkoss.spring.bean.TypePropertyEditor", "org.zkoss.spring.bean.TypePropertyEditor");
		xbuilder.addPropertyValue("customEditors", editors);
		reg.registerBeanDefinition(ZkBeanIds.ZK_TYPE_PROPERTY_EDITOR, xbuilder.getBeanDefinition());
	}

	private void registerImplicitObject(BeanDefinitionRegistry reg, String scope, String id, String type) {
		final BeanDefinitionBuilder builder =
				BeanDefinitionBuilder.rootBeanDefinition(ZkComponentFactoryBean.class);
		builder.setScope(scope); //application scope
		builder.addPropertyValue("type", type);
		reg.registerBeanDefinition(id, builder.getBeanDefinition());
	}

	//register ZK implicit object factory beans
	private void registerImplicitObjects(BeanDefinitionRegistry reg) {
		//application scope
		registerImplicitObject(reg, "application", "application", WebApp.class.getName());
		registerImplicitObject(reg, "application", "applicationScope", Map.class.getName());

		//session scope
		registerImplicitObject(reg, "session", "session", Session.class.getName());
		registerImplicitObject(reg, "session", "sessionScope", Map.class.getName());

		//desktop scope
		registerImplicitObject(reg, "desktop", "desktop", Desktop.class.getName());
		registerImplicitObject(reg, "desktop", "desktopScope", Map.class.getName());

		//page scope
		registerImplicitObject(reg, "page", "page", Page.class.getName());
		registerImplicitObject(reg, "page", "pageScope", Map.class.getName());

		//idspace scope
		registerImplicitObject(reg, "idspace", "spaceOwner", IdSpace.class.getName());
		registerImplicitObject(reg, "idspace", "spaceScope", Map.class.getName());
		registerImplicitObject(reg, "idspace", "componentScope", Map.class.getName());

		//execution scope
		registerImplicitObject(reg, "execution", "self", Component.class.getName());
		registerImplicitObject(reg, "execution", "execution", Execution.class.getName());
		registerImplicitObject(reg, "execution", "requestScope", Map.class.getName());
		registerImplicitObject(reg, "execution", "arg", Map.class.getName());
		registerImplicitObject(reg, "execution", "param", Map.class.getName());
		registerImplicitObject(reg, "execution", "event", Event.class.getName());
	}
}
