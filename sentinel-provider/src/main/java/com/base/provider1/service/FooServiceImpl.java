/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.base.provider1.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.base.api.FooService;

import java.time.LocalDateTime;

/**
 * @author
 */
@Service(
        version = "${foo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class FooServiceImpl implements FooService {

    @Override
    public String sayHello(String name) {
        return String.format("Hello, %s at %s", name, LocalDateTime.now());
    }

    @Override
    public String doAnother() {
        return LocalDateTime.now().toString();
    }
}
