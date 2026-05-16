package br.com.fiap.techchallenge.feedbackplatform.infrastructure.config;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class IntegrationTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "smallrye.jwt.encrypt.key.location", "publicKey.pem.example",
                "mp.jwt.verify.publickey.location", "publicKey.pem.example",
                "quarkus.native.resources.includes", "publicKey.pem.example");
    }
}
