package org.altfund.xchangeinterface.restApi.fillorkill;

import java.util.Map;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.altfund.xchangeinterface.util.JsonHelper;
import org.altfund.xchangeinterface.xchange.model.EncryptedOrder;
import org.altfund.xchangeinterface.xchange.model.Order;
import org.altfund.xchangeinterface.xchange.service.XChangeService;
import org.altfund.xchangeinterface.xchange.model.CurrenciesOnExchange;
import org.altfund.xchangeinterface.xchange.service.MessageEncryption;
import org.altfund.xchangeinterface.restApi.util.ResponseHandler;

/*
 * The above example does not specify GET vs. PUT, POST, and so forth, because
 * @RequestMapping maps all HTTP operations by default. Use
 * @RequestMapping(method=GET) to narrow this mapping.
 */
@Slf4j
@RestController
public class FillOrKillController {
    private final XChangeService xChangeService;
    private final JsonHelper jh;
    private final ResponseHandler rh;
    private final MessageEncryption messageEncryption;

    public FillOrKillController(XChangeService xChangeService, JsonHelper jh, ResponseHandler rh, MessageEncryption messageEncryption) {
        this.xChangeService = xChangeService;
        this.jh = jh;
        this.rh = rh;
        this.messageEncryption = messageEncryption;
    }

    @RequestMapping(value = "/fillorkill", produces = "application/json")
    public ResponseEntity<String> fillOrKill(@RequestParam Map<String, String> params) {
        List<Order> orders = null;
        String response = "";
        EncryptedOrder encryptedOrder = null;
        try {
            response = jh.getObjectMapper().writeValueAsString(params);
            encryptedOrder = jh.getObjectMapper().readValue(response, EncryptedOrder.class);

            orders = jh.getObjectMapper().readValue(messageEncryption.decrypt(encryptedOrder),
                                            new TypeReference<List<Order>>(){});
            response = xChangeService.fillOrKill(orders);

        }
        catch (JsonProcessingException ex) {
            return rh.send(ex, true);
        }
        catch (Exception ex) {
            return rh.send(ex, true);
        }
        //final HttpHeaders httpHeaders= new HttpHeaders();
        //httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //return new ResponseEntity<String>(response, httpHeaders, HttpStatus.OK);
        return rh.send(response, true);
    }
}
