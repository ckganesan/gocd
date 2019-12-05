/*
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.spark.spa;

import com.thoughtworks.go.server.service.GoConfigService;
import com.thoughtworks.go.spark.Routes;
import com.thoughtworks.go.spark.SparkController;
import com.thoughtworks.go.spark.spring.SPAAuthenticationHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class PipelineActivityController implements SparkController {
    private final SPAAuthenticationHelper authenticationHelper;
    private final TemplateEngine engine;
    private final GoConfigService goConfigService;

    public PipelineActivityController(SPAAuthenticationHelper authenticationHelper, TemplateEngine engine, GoConfigService goConfigService) {
        this.authenticationHelper = authenticationHelper;
        this.engine = engine;
        this.goConfigService = goConfigService;
    }

    @Override
    public String controllerBasePath() {
        return Routes.PipelineActivity.SPA_BASE;
    }

    @Override
    public void setupRoutes() {
        path(controllerBasePath(), () -> {
            before("", authenticationHelper::checkUserAnd403);
            get("", this::index, engine);
        });
    }

    public ModelAndView index(Request request, Response response) {
        String pipelineName = request.params("pipeline_name");
        Map<Object, Object> object = new HashMap<Object, Object>() {{
            put("viewTitle", "Pipeline Activity");
            put("meta", meta(pipelineName));
        }};
        return new ModelAndView(object, null);
    }

    private Map<String, Object> meta(String pipelineName) {
        final Map<String, Object> meta = new HashMap<>();

        meta.put("isEditableFromUI", goConfigService.isPipelineEditable(pipelineName));
        meta.put("pipelineName", pipelineName);

        return meta;
    }

}