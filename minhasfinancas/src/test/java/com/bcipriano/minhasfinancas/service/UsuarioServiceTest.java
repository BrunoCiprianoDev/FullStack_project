package com.bcipriano.minhasfinancas.service;

import com.bcipriano.minhasfinancas.exception.ErroAutenticacao;
import com.bcipriano.minhasfinancas.exception.RegraNegocioException;
import com.bcipriano.minhasfinancas.model.entity.Usuario;
import com.bcipriano.minhasfinancas.model.repository.UsuarioRepository;

import com.bcipriano.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

//@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;


    @Test(expected = Test.None.class)
    public void deveSalvarUmUsuario(){
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().id(1l).nome("nome").senha("senha").email("email@email.com").build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }
    @Test(expected = RegraNegocioException.class)
    public void naoDeveSalvarUsuarioComEmailJaCadastrado(){
        String email = "email@email.com";

        Usuario usuario = Usuario.builder().email(email).build();

        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        service.salvarUsuario(usuario);

        Mockito.verify(repository, Mockito.never()).save(usuario);
    }



    @Test(expected = Test.None.class)
    public void DeveAutenticarUsuarioComSucesso(){
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();

        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario result = service.autenticar(email,senha);

        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void deveLancarErrorQuandoNaoEncontrarUsuarioCadastradoPeloEmail(){
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Throwable exception = Assertions.catchThrowable(() ->service.autenticar("email@email.com", "senha"));

        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado!");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater(){

        Usuario usuario = Usuario.builder().email("email@email.com").senha("senha").build();

        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        Throwable exception = Assertions.catchThrowable(() ->service.autenticar("email@email.com", "senhaDiferente"));

        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");
    }

    @Test(expected = Test.None.class) //Expero que nenhuma exception seja lançada
    public void deveValidarEmail(){

        //Cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //Ação
        service.validarEmail("email@email.com");

    }
    @Test(expected = RegraNegocioException.class) //Aqui eu espero que ele lance uma exection
    public void deveLancarErroAoValidarQuandoExistirUmEmailCadastrado(){
        //Cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //Ação
        service.validarEmail("email@email.com");

    }

}
