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
package com.spindrift.gradle.config

class InvalidNamedConfigurationException extends RuntimeException {
  /**
   * Constructs an <code>InvalidNamedConfigurationException</code> with no
   * detail message.
   */
  public InvalidNamedConfigurationException() {
    super();
  }

  /**
   * Constructs an <code>InvalidNamedConfigurationException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public InvalidNamedConfigurationException(String s) {
    super(s);
  }
}
