package org.altfund.xchangeinterface.restApi.tradefees;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@RestController
public class TradeFeesController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;

    public TradeFeesController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
    }

    @RequestMapping(value = "/tradefees", produces = "application/json")
    public ResponseEntity<String> balance(@RequestParam Map<String, String> params) {
        ObjectNode json = xChangeService.getExchangeTradeFees(params);
        String response = "";
        try {
            response = jh.getObjectMapper().writeValueAsString(json);
        } catch (JsonProcessingException ex) {
            response = "{ERROR: JsonProcessingException "+ ex.getMessage() + "}";
        }
        return rh.send(response, true);
    }
}
