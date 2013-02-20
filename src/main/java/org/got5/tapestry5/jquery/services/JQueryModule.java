//
// Copyright 2010 GOT5 (GO Tapestry 5)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package org.got5.tapestry5.jquery.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Advise;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.compatibility.Compatibility;
import org.apache.tapestry5.services.compatibility.Trait;
import org.apache.tapestry5.services.javascript.JavaScriptModuleConfiguration;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.got5.tapestry5.jquery.EffectsConstants;
import org.got5.tapestry5.jquery.JQuerySymbolConstants;
import org.got5.tapestry5.jquery.services.impl.EffectsParamImpl;
import org.got5.tapestry5.jquery.services.impl.JGrowlManagerImpl;
import org.got5.tapestry5.jquery.services.impl.JavaScriptFilesConfigurationImpl;
import org.got5.tapestry5.jquery.services.impl.RenderTrackerImpl;
import org.got5.tapestry5.jquery.services.impl.WidgetParamsImpl;
import org.got5.tapestry5.jquery.services.js.JSModule;

@SubModule(JSModule.class)
public class JQueryModule {

	public static void contributeComponentClassResolver(
			Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("jquery",
				"org.got5.tapestry5.jquery"));
	}

	@Contribute(SymbolProvider.class)
	@FactoryDefaults
	public static void contributeFactoryDefaults(
			MappedConfiguration<String, Object> configuration) {

		configuration.add(JQuerySymbolConstants.JQUERY_VERSION, "1.8.2");
		configuration.add(JQuerySymbolConstants.JQUERY_UI_VERSION, "1.9.2");
		configuration.add(JQuerySymbolConstants.JQUERY_JSON_VERSION, "2.4");

		configuration.add(JQuerySymbolConstants.ASSETS_ROOT,
				"classpath:/META-INF/modules/tjq");
		configuration.add(JQuerySymbolConstants.JQUERY_UI_PATH,
				"${jquery.assets.root}/vendor/ui");
		configuration.add(JQuerySymbolConstants.ASSETS_PATH,
				"${jquery.assets.root}/lib");

		configuration.add(JQuerySymbolConstants.JQUERY_UI_DEFAULT_THEME,
				"${jquery.ui.path}/themes/smoothness/jquery-ui.css");

		configuration.add(JQuerySymbolConstants.ADD_MOUSEWHEEL_EVENT, false);

		configuration.add(JQuerySymbolConstants.SUPPRESS_PROTOTYPE, true);

		// MIGRATION TO 5.4
		configuration.add(JQuerySymbolConstants.TAPESTRY_JQUERY_PATH,
				"classpath:org/got5/tapestry5/jquery");
		configuration.add(JQuerySymbolConstants.TAPESTRY_JS_PATH,
				"classpath:org/got5/tapestry5/tapestry.js");
		configuration
				.add(JQuerySymbolConstants.JQUERY_CORE_PATH,
						"classpath:org/got5/tapestry5/jquery/jquery_core/jquery-1.7.2.js");

		configuration.add(JQuerySymbolConstants.JQUERY_VALIDATE_PATH,
				"classpath:org/got5/tapestry5/jquery/validate/1_7");

		configuration.add(JQuerySymbolConstants.JQUERY_ALIAS, "$");

		configuration.add(JQuerySymbolConstants.PARAMETER_PREFIX, "tjq-");
		configuration.add(JQuerySymbolConstants.USE_MINIFIED_JS,
				SymbolConstants.PRODUCTION_MODE_VALUE);

	}

	@Contribute(Compatibility.class)
	public static void contributeCompatibility(
			MappedConfiguration<Trait, Object> configuration,
			@Symbol(JQuerySymbolConstants.SUPPRESS_PROTOTYPE) Boolean prototype) {

		if (prototype)
			configuration.add(Trait.SCRIPTACULOUS, false);
	}

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void contributeApplicationDefault(
			MappedConfiguration<String, Object> configuration) {

		configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER,
				"jquery");
	}

	public static void contributeClasspathAssetAliasManager(
			MappedConfiguration<String, String> configuration) {
		configuration.add("tap-jquery", "org/got5/tapestry5");
		configuration.add("tapestry-jquery", "META-INF/modules/tjq/vendor");
	}

	public static void contributeBindingSource(
			MappedConfiguration<String, BindingFactory> configuration,
			@InjectService("SelectorBindingFactory") BindingFactory selectorBindingFactory) {
		configuration.add("selector", selectorBindingFactory);

	}

	public static void bind(ServiceBinder binder) {
		binder.bind(WidgetParams.class, WidgetParamsImpl.class);
		binder.bind(EffectsParam.class, EffectsParamImpl.class);
		binder.bind(BindingFactory.class, SelectorBindingFactory.class).withId(
				"SelectorBindingFactory");
		binder.bind(RenderTracker.class, RenderTrackerImpl.class);
		binder.bind(AjaxUploadDecoder.class, AjaxUploadDecoderImpl.class)
				.scope(ScopeConstants.PERTHREAD);
		binder.bind(JavaScriptFilesConfiguration.class,
				JavaScriptFilesConfigurationImpl.class);
		binder.bind(JGrowlManager.class, JGrowlManagerImpl.class);
	}

	/**
	 * By Default, we import the JavaScript file of the HighLight Effect.
	 * 
	 * @param configuration
	 */
	@Contribute(EffectsParam.class)
	public void addEffectsFile(Configuration<String> configuration) {
		configuration.add(EffectsConstants.HIGHLIGHT);
		configuration.add(EffectsConstants.SHOW);
	}

	@Contribute(ComponentClassTransformWorker2.class)
	@Primary
	public static void addWorker(
			OrderedConfiguration<ComponentClassTransformWorker2> configuration,
			@Symbol(JQuerySymbolConstants.SUPPRESS_PROTOTYPE) boolean suppressPrototype) {

		// if (suppressPrototype) {
		// configuration.addInstance("FormResourcesInclusionWorker",
		// FormResourcesInclusionWorker.class, "after:RenderPhase");
		// }
		configuration.addInstance("RenderTrackerMixinWorker",
				RenderTrackerMixinWorker.class);

		// note: the ordering must ensure that the worker gets added after the
		// RenderPhase-Worker!
		// configuration.addInstance("DateFieldWorker", DateFieldWorker.class,
		// "after:RenderPhase");
		configuration.addInstance("ImportJQueryUIWorker",
				ImportJQueryUIWorker.class, "before:Import",
				"after:RenderPhase");
	}

	public static void contributeHttpServletRequestHandler(
			final OrderedConfiguration<HttpServletRequestFilter> configuration,
			final AjaxUploadDecoder ajaxUploadDecoder) {

		configuration.add("AjaxUploadFilter",
				new AjaxUploadServletRequestFilter(ajaxUploadDecoder),
				"after:IgnoredPaths");
	}

//	@Contribute(ModuleManager.class)
//	public static void setupjQueryUIShims(
//			MappedConfiguration<String, Object> configuration,
//			@Symbol(JQuerySymbolConstants.ADD_MOUSEWHEEL_EVENT) boolean mouseWheelIncluded,
//			@Inject @Path("${jquery.ui.path}/ui/jquery-ui.custom.js") Resource jqueryui,
//			@Inject @Path("${jquery.ui.path}/ui/jquery.ui.effect.js") Resource jqueryuieffect,
//			@Inject @Path("${jquery.assets.root}/jquery.json-2.4.js") Resource jqueryjson,
//			@Inject @Path("${jquery.ui.path}/external/jquery.mousewheel.js") Resource jquerymousewheel) {
//
//		configuration.add("vendor/jqueryui", new JavaScriptModuleConfiguration(
//				jqueryui).dependsOn("jquery"));
//
//		configuration.add("vendor/jqueryjson",
//				new JavaScriptModuleConfiguration(jqueryjson)
//						.dependsOn("jquery"));
//
//		configuration.add("vendor/jqueryuieffect",
//				new JavaScriptModuleConfiguration(jqueryuieffect)
//						.dependsOn("jquery"));
//
//	}



	@Contribute(ModuleManager.class)
	public static void setupComponentsShims(
			MappedConfiguration<String, Object> configuration,
			@Inject @Path("/META-INF/modules/tjq/datefield.js") Resource datefield) {

		
		configuration.add("t5/core/datefield",
				new JavaScriptModuleConfiguration(datefield));

	}
}
