/**
 * Copyright (c) 2010 RedEngine Ltd, http://www.redengine.co.nz. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package net.stickycode.mockwire.guice2;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Provider;
import com.google.inject.spi.TypeEncounter;

import net.stickycode.mockwire.Bless;
import net.stickycode.mockwire.Controlled;
import net.stickycode.mockwire.Mock;
import net.stickycode.mockwire.UnderTest;
import net.stickycode.reflector.AnnotatedFieldProcessor;
import net.stickycode.reflector.ValueSource;

public class Values
  extends AnnotatedFieldProcessor
    implements ValueSource {

  private final Map<Class<?>, Provider<?>> providers = new HashMap<Class<?>, Provider<?>>();
  private final TypeEncounter<?> encounter;

  public Values(TypeEncounter<?> encounter) {
    super(Bless.class, UnderTest.class, Mock.class, Controlled.class);
    this.encounter = encounter;
  }

  @Override
  public Object get(Class<?> type) {
    return providers.get(type).get();
  }

  @Override
  public void processField(Object target, Field field) {
    providers.put(field.getType(), encounter.getProvider(field.getType()));
  }
}
