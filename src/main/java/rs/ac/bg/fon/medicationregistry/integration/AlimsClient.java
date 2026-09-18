package rs.ac.bg.fon.medicationregistry.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import rs.ac.bg.fon.medicationregistry.dtos.AlimsResponseDto;
import rs.ac.bg.fon.medicationregistry.exceptions.AlimsUnavailableException;


@Component
public class AlimsClient {

    private final RestClient restClient;
    private final String apiUrl;

    public AlimsClient(RestClient restClient, @Value("${alims.api.url}") String apiUrl) {
        this.restClient = restClient;
        this.apiUrl = apiUrl;
    }

    public AlimsResponseDto fetchMedications(){
        AlimsResponseDto response = restClient.get().uri(apiUrl).retrieve().body(AlimsResponseDto.class);

        if (response == null || response.lekovi() == null) {
            throw new AlimsUnavailableException("ALIMS nije vratio ocekivani odgovor");
        }
        return response;
    }
}
