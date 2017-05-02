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
        messagingService.sendReportAcknowledgement(UUID.fromString(state.getExamId()), state);
    }
}
