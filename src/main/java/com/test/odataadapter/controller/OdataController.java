package com.test.odataadapter.controller;

import com.test.odataadapter.service.CarsEdmProvider;
import com.test.odataadapter.service.EdmProvider;
import com.test.odataadapter.service.HandlerImpl;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
@RequestMapping("api")
public class OdataController {
    @RequestMapping(value="**",produces = { "application/xml", "text/xml" })
    public void handleMetaData(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ODataHttpHandler handler = new HandlerImpl().build();//odata.createHandler(edm1);
            handler.process(req, resp);
        } catch (RuntimeException e) {
        }
    }


}
