package guru.springframework.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.model.BeerDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class BeerClientImpl implements BeerClient {

    public static final String BEER_PATH = "/api/v3/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final WebClient webClient;

    public BeerClientImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Flux<String> listBeer() {
        return webClient.get().uri(BEER_PATH)
            .retrieve()
            .bodyToFlux(String.class);
    }

    @Override
    public Flux<Map> listBeerMap() {
        return webClient.get().uri(BEER_PATH)
            .retrieve()
            .bodyToFlux(Map.class);
    }

    @Override
    public Flux<JsonNode> listBeersJsonNode() {
        return webClient.get().uri(BEER_PATH)
            .retrieve()
            .bodyToFlux(JsonNode.class);
    }

    @Override
    public Flux<BeerDTO> listBeersDTO() {
        return webClient.get().uri(BEER_PATH)
            .retrieve()
            .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> getBeerById(String beerId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(beerId))
            .retrieve()
            .bodyToMono(BeerDTO.class);
    }

    @Override
    public Flux<BeerDTO> getBeerByStyle(String beerStyle) {
        return webClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .path(BEER_PATH)
                .queryParam("beerStyle", beerStyle)
                .build())
            .retrieve()
            .bodyToFlux(BeerDTO.class)
            .doOnError(e -> {
                throw new NoSuchElementException("No element found!");
            });
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO beerDTO) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(BEER_PATH).build())
            .body(Mono.just(beerDTO), BeerDTO.class)
            .retrieve()
            .toBodilessEntity()
            .flatMap(voidResponseEntity -> Mono.just(voidResponseEntity.getHeaders().get("Location").get(0)))
            .map(path -> path.split("/")[path.split("/").length - 1])
            .flatMap(this::getBeerById);
    }

    @Override
    public Mono<BeerDTO> updateBeer(BeerDTO beerDTO, String id) {
        return webClient.put()
            .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(id))
            .body(Mono.just(beerDTO), BeerDTO.class)
            .retrieve()
            .toBodilessEntity()
            .flatMap(voidResponseEntity -> Mono.just(voidResponseEntity.getHeaders().get("Location").get(0)))
            .map(path -> path.split("/")[path.split("/").length - 1])
            .flatMap(this::getBeerById);
    }

    @Override
    public Mono<BeerDTO> patchBeer(BeerDTO beerDTO, String id) {
        return webClient.patch()
            .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(id))
            .body(Mono.just(beerDTO), BeerDTO.class)
            .retrieve()
            .bodyToMono(BeerDTO.class);
    }

    @Override
    public Mono<Void> deleteBeer(String id) {
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder.path(BEER_PATH_ID).build(id))
            .retrieve()
            .toBodilessEntity().then();
    }
}
