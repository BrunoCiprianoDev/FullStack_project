package com.bcipriano.minhasfinancas.service.impl;

import com.bcipriano.minhasfinancas.exception.RegraNegocioException;
import com.bcipriano.minhasfinancas.model.entity.Usuario;
import com.bcipriano.minhasfinancas.model.repository.UsuarioRepository;
import com.bcipriano.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;

public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository repository){
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        return null;
    }

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        return null;
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if(existe){
            throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail.");
        }
    }
}
