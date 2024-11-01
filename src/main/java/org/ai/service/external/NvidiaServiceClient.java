package org.ai.service.external;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.ai.integration.types.Records;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/chat")
@RegisterRestClient(configKey = "nvidia-ai-service")
public interface NvidiaServiceClient {

    @POST
    @Path("/completions")
    public Records.NvidiaAIResponse completion(
            @HeaderParam("Authorization") String token,
            Records.NvidiaAIRequest request
    );
}
