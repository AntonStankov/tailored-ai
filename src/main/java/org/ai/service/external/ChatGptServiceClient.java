package org.ai.service.external;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.ai.integration.types.Records;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1/chat")
@RegisterRestClient(configKey = "chat-gpt-service")
public interface ChatGptServiceClient {

    @POST
    @Path("/completions")
    public Records.ChatGptResponse completion(
            @HeaderParam("Authorization") String token,
            Records.ChatGptRequest request
    );
}
