//  Configuration.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.util;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This class contain types and constant definitions
 */
public class Configuration implements Serializable {

  /** 
   * Logger object : Logger 对象用来记录特定系统或应用程序组件的日志消息
   * getLogger(String name) :
   *       为指定子系统查找或创建一个 logger。
   */
  public static Logger logger_ = Logger.getLogger("learn_jmetal");
    
} // Configuration
