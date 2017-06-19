/*******************************************************************************
 * Copyright 2017 Regents of the University of California. Licensed under the Educational
 * Community License, Version 2.0 (the “license”); you may not use this file except in
 * compliance with the License. You may obtain a copy of the license at
 *
 * https://opensource.org/licenses/ECL-2.0
 *
 * Unless required under applicable law or agreed to in writing, software distributed under the
 * License is distributed in an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for specific language governing permissions
 * and limitations under the license.
 *
 ******************************************************************************/

package tds.exam.results.web.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import tds.exam.results.services.MessagingService;
import tds.exam.results.tis.TISState;

@RestController
public class TISCallbackController {
    private final MessagingService messagingService;

    @Autowired
    public TISCallbackController(final MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @RequestMapping(value = "/tis", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void tisCallback(@RequestBody final TISState state) {
        messagingService.sendReportAcknowledgement(UUID.fromString(state.getOppKey()), state);
    }
}
