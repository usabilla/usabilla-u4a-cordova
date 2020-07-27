
  /*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/// Usabilla Configuration
// Replace appId with your usabilla app id.
const appId = 'YOUR_APP_ID_HERE';
// Replace FormId with your usabilla form id.
const formId = 'YOUR_FORM_ID_HERE';

/// To use event from here at initEvent line #87 at index.js
/// Don't put anything into inputbox event will be overriden.
// Replace event with your usabilla campaign event tag created for targeting specific Campaign.
let event = 'YOUR_EVENT_TAG_HERE';

// Replace custom variable with your usabilla custom variable created for targeting specific Campaign..
const customVariable = {'YOUR_KEY_HERE': 'YOUR_VALUE_HERE'};
