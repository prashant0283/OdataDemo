package com.test.odataadapter.controller;

import com.test.odataadapter.service.CarsEdmProvider;
import com.test.odataadapter.service.EdmProvider;
import com.test.odataadapter.service.HandlerImpl;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class CarsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CarsServlet.class);
    ODataHttpHandler handler;

    public CarsServlet(){
        //handler = HandlerImpl.build();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //OData odata = OData.newInstance();
        	
            //ServiceMetadata edm1 = odata.createServiceMetadata(new CarsEdmProvider(), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = new HandlerImpl().build();//odata.createHandler(edm1);

            handler.process(req, resp);
        } catch (RuntimeException e) {
            LOG.error("Server Error", e);
            throw new ServletException(e);
        }
    }
}
