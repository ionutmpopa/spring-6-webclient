package guru.springframework.client;

import guru.springframework.model.BeerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    @Test
    @Order(5)
    void listBeer() {

        AtomicReference<String> atomicReference = new AtomicReference<>();

        beerClient.listBeer().subscribe(beers -> {
            System.out.println(beers);
            atomicReference.set(beers);
        });

        await().until(() -> atomicReference.get() != null);

    }

    @Test
    @Order(6)
    void listBeerMap() {

        AtomicReference<Map> atomicReference = new AtomicReference<>();

        beerClient.listBeerMap().subscribe(beers -> {
            System.out.println(beers);
            atomicReference.set(beers);
        });

        await().until(() -> atomicReference.get() != null);

    }

    @Test
    @Order(7)
    void testGetBeerJson() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerClient.listBeersJsonNode().subscribe(jsonNode -> {

            System.out.println(jsonNode.toPrettyString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(8)
    void testGetBeerDTO() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerClient.listBeersDTO().subscribe(beerDTO -> {

            System.out.println(beerDTO.toString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(9)
    void testGetBeerById() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerClient.listBeersDTO()
            .flatMap(beerDTO -> beerClient.getBeerById(beerDTO.getId()))
            .subscribe(beerDTOById -> {
                System.out.println(beerDTOById.getBeerName());
                atomicBoolean.set(true);
            });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(4)
    void testGetBeerByStyle() {

        AtomicReference<BeerDTO> atomicReference = new AtomicReference<>();

        beerClient.getBeerByStyle("IPA")
            .onErrorResume(Mono::error)
            .subscribe(beerDTO -> {
                System.out.println(beerDTO.getBeerName());
                atomicReference.set(beerDTO);
            });

        await().until(() -> atomicReference.get() != null);
    }

    @Test
    @Order(1)
    void testCreateBeer() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        BeerDTO newDto = BeerDTO.builder()
            .price(new BigDecimal("10.99"))
            .beerName("Mango Bobs")
            .beerStyle("IPA")
            .quantityOnHand(500)
            .upc("123245")
            .build();

        beerClient.createBeer(newDto)
            .subscribe(dto -> {
                System.out.println(dto.toString());
                atomicBoolean.set(true);
            });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(2)
    void updateBeer() {

        String name = "NEW BEER";

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerClient.listBeersDTO()
            .next()
            .doOnNext(beerDTO -> beerDTO.setBeerName(name))
            .flatMap(beerDTO -> beerClient.updateBeer(beerDTO, beerDTO.getId()))
            .subscribe(beerDTO -> {
                System.out.println(beerDTO.getBeerName());
                atomicBoolean.set(true);
            });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(3)
    void patchBeer() {

        String name = "NEW OLD BEER";

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerClient.listBeersDTO()
            .next()
            .doOnNext(beerDTO -> beerDTO.setBeerName(name))
            .flatMap(beerDTO -> beerClient.patchBeer(beerDTO, beerDTO.getId()))
            .subscribe(beerDTO -> {
                System.out.println(beerDTO);
                atomicBoolean.set(true);
            });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @Order(10)
    void deleteBeer() {

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerClient.listBeersDTO()
            .next()
            .flatMap(beerDTO -> beerClient.deleteBeer(beerDTO.getId()))
            .doOnSuccess(beerDTO -> {
                atomicBoolean.set(true);
            })
            .subscribe();

        await().untilTrue(atomicBoolean);
    }


}
