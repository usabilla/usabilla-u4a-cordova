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
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.initApp();
    },
    initApp: function() {
        var self = this;
        Usabilla.initialize(
            function(res) {
                console.log('Init - ',JSON.stringify(res));
            }, 
            function (err) {
                console.log('Init - ',JSON.stringify(err));
            },
            appId,
            customVariable
        );
    },
    initForm: function() {
        var self = this;
        var sendFeedback = document.getElementById("feedback-button");
        sendFeedback.onclick = function() {
            if (!formId) {
                alert("Input a form id to submit");
            } else {
                Usabilla.loadFeedbackForm(
                    function(res) {
                        console.log('Form - '+JSON.stringify(res));
                    }, 
                    function (err) {
                        console.log('Form - '+JSON.stringify(err));
                    },
                    formId);
            }
        }
    },
    initResetCampaign: function() {
        var self = this;
        var resetEvent = document.getElementById("reset-button");
        resetEvent.onclick = function() {
            Usabilla.resetCampaignData(
                function(res) {
                    console.log('Reset - '+JSON.stringify(res));
                }, 
                function (err) {
                    console.log('Reset - '+JSON.stringify(err));
                },
            );
        };
    },
    initEvent: function() {
        var self = this;
        var sendEvent = document.getElementById("event-button");
        sendEvent.onclick = function() {
            var eventId = document.getElementById("event-id").value;
            if (eventId !== "") {
                event = eventId;
            }
            if (!event) {
                alert("Input an event id to submit");
            } else {
                Usabilla.sendEvent(
                    function(res) {
                        console.log('Event - '+JSON.stringify(res));
                    }, 
                    function (err) {
                        console.log('Event - '+JSON.stringify(err));
                    },
                    event);
            }
        }
    }
};

app.initialize();
