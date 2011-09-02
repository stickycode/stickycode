/**
 * Copyright (c) 2011 RedEngine Ltd, http://www.RedEngine.co.nz. All rights reserved.
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
package net.stickycode.scheduled.configuration;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.stickycode.scheduled.Schedule;


public class ScheduleParser {
  
  Pattern pattern = Pattern.compile("every ([0-9]+)? ?([a-zA-Z]+)");

  Schedule parse(String value) {
    Matcher match = pattern.matcher(value);
    if (!match.matches())
      throw new ScheduleDefintionIsNotValidException(value); 
      
    TimeUnit unit = parseUnit(match.group(2));
    long period = parsePeriod(match.group(1));
    return new Schedule(0, TimeUnit.SECONDS.convert(period, unit));
  }

  private long parsePeriod(String group) {
    if (group == null)
      return 1;
    
    return Long.valueOf(group);
  }

  TimeUnit parseUnit(String period) {
    try {
      if (period.endsWith("s") || period.endsWith("S"))
        return TimeUnit.valueOf(period.toUpperCase(Locale.US));
      
      return TimeUnit.valueOf(period.toUpperCase(Locale.US) + "S");
    }
    catch (IllegalArgumentException e) {
      throw new UnknownUnitForSchedulingException(e, period);
    }
  }

}
