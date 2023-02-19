package com.bcipriano.minhasfinancas.model.repository;

import com.bcipriano.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

//@SpringBootTest // Faz o test subindo toda a aplicação
@RunWith(SpringRunner.class)
@ActiveProfiles("test") // Redireciona para a base de dados de teste
@DataJpaTest // Ela cria um instancia do banco de dados em memória apenas para realizar teste
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {
    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager; //Realiza as operações na base de dados

    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
        //Cenario (Salvar um novo usuario)
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //Ação (Buscar pelo email do usuário salvo)
        boolean result = repository.existsByEmail("usuario@email.com");

        //Verificação (Verificar se a busca pelo email resultou em true)
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastroComOEmail(){

        //Ação(Verifica se existe o email)
        boolean result = repository.existsByEmail("usuario@email.com");

        //Verificação (Observa se a resposta é 'false' como o esperado)
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados() {

        //Cenário
        Usuario usuario = criarUsuario();

        //Ação
        Usuario usuarioSalvo = repository.save(usuario);

        //Verificar
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase(){

        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        Assertions.assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){

        //Cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //Verificação
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        Assertions.assertThat(result.isPresent()).isTrue();
    }


    public static Usuario criarUsuario(){
        return Usuario.builder()
                .nome("usuario")
                .senha("senha")
                .email("usuario@email.com").build();
    }

}
