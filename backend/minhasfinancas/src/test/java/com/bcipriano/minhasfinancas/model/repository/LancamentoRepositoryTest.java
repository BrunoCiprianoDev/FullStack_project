package com.bcipriano.minhasfinancas.model.repository;

import com.bcipriano.minhasfinancas.model.entity.Lancamento;
import com.bcipriano.minhasfinancas.model.entity.enums.StatusLancamento;
import com.bcipriano.minhasfinancas.model.entity.enums.TipoLancamento;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamento = criarInstanciaLancamentoParaTeste();

        Lancamento lancamentoSalvo = lancamentoRepository.save(lancamento);

        Assertions.assertThat(lancamentoSalvo.getId()).isNotNull();

    }

    @Test
    public void deveDeletarLancamento() {

        Lancamento lancamento = criarInstanciaLancamentoParaTeste();

        entityManager.persist(lancamento);

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        lancamentoRepository.delete(lancamento);

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamento).isNull();

    }

    @Test
    public void deveAtualizarUmLancamento(){

        Lancamento lancamento = criarInstanciaLancamentoParaTeste();

        entityManager.persist(lancamento);

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        lancamento.setAno(2018);
        lancamento.setMes(2);
        lancamento.setStatus(StatusLancamento.CANCELADO);
        lancamento.setTipo(TipoLancamento.DESPESA);
        lancamento.setDescricao("RandomEdit");

        lancamento = lancamentoRepository.save(lancamento);

        Lancamento lancamentoUpdate = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamento.equals(lancamentoUpdate)).isTrue();

    }

    public static Lancamento criarInstanciaLancamentoParaTeste() {
        return Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("Random")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

}
