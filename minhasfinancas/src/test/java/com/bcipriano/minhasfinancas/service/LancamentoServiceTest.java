package com.bcipriano.minhasfinancas.service;

import com.bcipriano.minhasfinancas.exception.RegraNegocioException;
import com.bcipriano.minhasfinancas.model.entity.Lancamento;
import com.bcipriano.minhasfinancas.model.entity.Usuario;
import com.bcipriano.minhasfinancas.model.entity.enums.StatusLancamento;
import com.bcipriano.minhasfinancas.model.entity.enums.TipoLancamento;
import com.bcipriano.minhasfinancas.model.repository.LancamentoRepository;
import com.bcipriano.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.bcipriano.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.nio.channels.MembershipKey;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl lancamentoService;

    @MockBean
    LancamentoRepository lancamentoRepository;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamentoSalvar = criarInstanciaLancamentoParaTeste();

        Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvar);

        Lancamento lancamentoSalvo = criarInstanciaLancamentoParaTeste();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(lancamentoRepository.save(lancamentoSalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = lancamentoService.salvar(lancamentoSalvar);

        Assertions.assertThat(lancamento.getId().equals(lancamentoSalvo.getId()));
        Assertions.assertThat(lancamento.getStatus().equals(StatusLancamento.PENDENTE));

    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {

        Lancamento lancamentoSalvar = criarInstanciaLancamentoParaTeste();

        Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoSalvar);

        Assertions.catchThrowableOfType(() -> lancamentoService.salvar(lancamentoSalvar), RegraNegocioException.class);

        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoSalvar);

    }

    @Test
    public void deveAtualizaLancamento() {

        Lancamento lancamentoSalvo = criarInstanciaLancamentoParaTeste();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvo);

        Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        lancamentoService.atualizar(lancamentoSalvo);

        Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);

    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamentoSalvar = criarInstanciaLancamentoParaTeste();

        Assertions.catchThrowableOfType(() -> lancamentoService.atualizar(lancamentoSalvar), NullPointerException.class);

        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoSalvar);
    }

    @Test
    public void deveDeletarUmLancamento() {

        Lancamento lancamento = criarInstanciaLancamentoParaTeste();
        lancamento.setId(1l);

        lancamentoService.deletar(lancamento);

        Mockito.verify(lancamentoRepository).delete(lancamento);

    }

    @Test
    public void naoLancarErroAoTentarDeletarUMLancamentoQueAindaNaoFoiSalvo() {

        Lancamento lancamento = criarInstanciaLancamentoParaTeste();

        Assertions.catchThrowableOfType(() -> lancamentoService.deletar(lancamento), NullPointerException.class);

        Mockito.verify(lancamentoRepository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamento(){

        Lancamento lancamento = LancamentoRepositoryTest.criarInstanciaLancamentoParaTeste();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);

        Mockito.when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = lancamentoService.buscar(lancamento);

        Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);

    }

    @Test
    public void deveAtualizarStatusDeUmLancamento(){

        Lancamento lancamento = criarInstanciaLancamentoParaTeste();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;

        lancamentoService.atualizarStatus(lancamento, novoStatus );

        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(lancamentoService).atualizar(lancamento);

    }

    @Test
    public void deveObterLancamentoPorId() {

        Long id = 1l;

        Lancamento lancamento = criarInstanciaLancamentoParaTeste();
        lancamento.setId(1l);

        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isTrue();

    }

    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste(){

        Long id = 1l;

        Lancamento lancamento = criarInstanciaLancamentoParaTeste();
        lancamento.setId(id);

        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isFalse();

    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento(){

        Lancamento lancamento = new Lancamento();

        //Verificação do atributo descricao:
        Throwable erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida!");
        lancamento.setDescricao("");

        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida!");
        lancamento.setDescricao("random-description");

        //Verificação do atributo mes
        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
        lancamento.setMes(0);

        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
        lancamento.setMes(13);

        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
        lancamento.setMes(1);

        //Verificação do atributo ano
        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
        lancamento.setAno(999);

        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
        lancamento.setAno(10000);

        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
        lancamento.setAno(2023);

        //Verifição do atributo Usuario
        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário!");
        lancamento.setUsuario(Usuario.builder().id(1l).build());

        //Verificação do atributo valor
        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido!");
        lancamento.setValor(BigDecimal.valueOf(1));

        //Verificação do atributo TipoLancamento
        erro = Assertions.catchThrowable(()-> lancamentoService.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lancamento!");
        lancamento.setTipo(TipoLancamento.RECEITA);

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
