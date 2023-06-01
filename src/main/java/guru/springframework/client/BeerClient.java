package guru.springframework.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BeerClient {

    Flux<String> listBeer();

    Flux<Map> listBeerMap();

    Flux<JsonNode> listBeersJsonNode();

    Flux<BeerDTO> listBeersDTO();

    Mono<BeerDTO>getBeerById(String beerId);

    Mono<BeerDTO> createBeer(BeerDTO beerDTO);

    Flux<BeerDTO> getBeerByStyle(String beerStyle);

    Mono<BeerDTO> updateBeer(BeerDTO beerDTO, String id);

    Mono<BeerDTO> patchBeer(BeerDTO beerDTO, String id);

    Mono<Void> deleteBeer(String id);
}
