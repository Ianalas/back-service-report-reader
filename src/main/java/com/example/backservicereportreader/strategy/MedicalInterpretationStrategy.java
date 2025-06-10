package com.example.backservicereportreader.strategy;

import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@NoArgsConstructor
public class MedicalInterpretationStrategy implements InterpretationStrategy {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final HttpClient client = HttpClient.newHttpClient();


    @Override
    public String interpret(List<String> lines) {
        if (apiKey == null || apiKey.equals("SUA_CHAVE_API_AQUI") || apiKey.trim().isEmpty()) {
            return "Erro: A chave da API do Gemini não foi configurada no arquivo application.properties ou está inválida.";
        }

        String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;


        String fullText = String.join("\n", lines);

        String promptText = """
                Lembre-se que você é um especialista em medicina que deverá
                resumir de forma fácil a partir de dados passados abaixo,
                o que está sendo falado naqueles dados mostrados,
                e por fim divida em dois tópicos (Explicação e Conclusão):

                %s
                """.formatted(fullText);

        try {
            // Body request JSON API Gemini
            String requestBody = new JSONObject()
                    .put("contents", new JSONObject[] {
                            new JSONObject().put("parts", new JSONObject[] {
                                    new JSONObject().put("text", promptText)
                            })
                    })
                    .toString();

            // Request POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(geminiApiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractTextFromResponse(response.body());
            } else {
                return "Erro ao chamar a API do Gemini. Status: " + response.statusCode() + " | Resposta: " + response.body();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Ocorreu uma exceção interna ao tentar contatar o serviço: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        try {
            JSONObject responseObject = new JSONObject(jsonResponse);
            return responseObject.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (Exception e) {
            return "Não foi possível extrair o texto da resposta: " + jsonResponse + " | Erro: " + e.getMessage();
        }
    }
}