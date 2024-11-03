package org.ai.service.external;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@RegisterRestClient(configKey = "history-nginx")
public interface HistoryNginxClient {

    @GET
    @Path("/{fileName}")
    @Produces(MediaType.TEXT_PLAIN)
    String getFileContent(@PathParam("fileName") String fileName);
}
