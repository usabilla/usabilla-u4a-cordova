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
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
        this.initForm();
        this.initEvent();
        this.initResetCampaign();
        this.receivedEvent('deviceready');
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
        this.initApp();
    },

    setButtonsDisabled: function(disabled) {
        document.querySelectorAll('button').forEach(item => item.disabled = disabled);
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = document.querySelectorAll('.received');

        listeningElement.setAttribute('style', 'display:none;');

        receivedElement.forEach(function(element) {
            element.setAttribute('style', 'display:block;');
        })
    },

    initApp: function() {
        this.setButtonsDisabled(true);
        var self = this;
        var appId = "69439731-5ed7-4abb-b5d1-974b5a4ccc1b";
        var customVars = {
            type: 'premium'
        };
        Usabilla.initialize(
            function() {
                self.setButtonsDisabled(false);
            },
            function () {
                self.setButtonsDisabled(false);
            },
            appId,
            customVars
        )
    },
    initForm: function() {
        var self = this;
        var sendFeedback = document.getElementById("feedback-button");
        sendFeedback.onclick = function() {
            self.setButtonsDisabled(false);
            var formId = document.getElementById("form-id").value;

            if (!formId) {
                alert("Input a form id to submit");
            } else {
                var customVars = {
                    fromExampleApp: true,
                    foo: 'var'
                };
                Usabilla.feedback(
                    function() {
                        self.setButtonsDisabled(false);
                    }, 
                    function () {
                        self.setButtonsDisabled(false);
                    },
                    formId);
            }
        }
    },
    initResetCampaign: function() {
        var self = this;
        var resetEvent = document.getElementById("reset-button");
        resetEvent.onclick = function() {
            self.setButtonsDisabled(true);
            Usabilla.resetCampaignData(
                function() {
                    self.setButtonsDisabled(false);
                },
                function() {
                    self.setButtonsDisabled(false);
                }
            );
        };
    },
    initEvent: function() {
        var self = this;
        var sendEvent = document.getElementById("event-button");
        sendEvent.onclick = function() {
            var eventId = document.getElementById("event-id").value;
            var appId = "69439731-5ed7-4abb-b5d1-974b5a4ccc1b";
            self.setButtonsDisabled(true);
            if (!eventId) {
                alert("Input a form id to submit");
            } else {
                
                Usabilla.sendEvent(
                    function() {
                        self.setButtonsDisabled(false);
                    }, 
                    function () {
                        self.setButtonsDisabled(false);
                    },
                    eventId);
            }
        }
    }
};

app.initialize();