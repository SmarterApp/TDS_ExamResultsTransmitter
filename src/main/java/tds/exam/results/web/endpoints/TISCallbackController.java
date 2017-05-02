package tds.exam.results.web.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

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
    public void tisCallback(@RequestBody final TISState state, final HttpServletResponse response) {
        if (state.getExamId() == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        try {
            // Verify that the examId string sent across is in fact a UUID. No need to actually store the value, however,
            // since it would be converted right back to a string for RabbitMQ consumption.
            UUID.fromString(state.getExamId());
            messagingService.sendReportAcknowledgement(state);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
}
