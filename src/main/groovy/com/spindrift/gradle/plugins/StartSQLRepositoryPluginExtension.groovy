/**
 * Copyright (C) 2012-2017 Spindrift B.V. All Rights Reserved
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
package com.spindrift.gradle.plugins

import com.spindrift.gradle.config.ConfigurationsContainer
import org.gradle.util.ConfigureUtil

/**
 * Provides configuration for the startSQLRepository wrapper plugin
 * All properties can be set directly or using its DSL equivalent
 * e.g.
 * <code>
 * startSQLRepository {
 *   property = "xxx" //Direct setting
 *   property "xxx"   //Optional DSL style setting
 * }
 * </code>
 *
 * @author hallatech
 *
 */

class StartSQLRepositoryPluginExtension {

  String name
  def name(String name) {
    name = name
  }

  ConfigurationsContainer configurations = new ConfigurationsContainer()
  void configurations(Closure closure) {
    ConfigureUtil.configure(closure, configurations)
  }
}
