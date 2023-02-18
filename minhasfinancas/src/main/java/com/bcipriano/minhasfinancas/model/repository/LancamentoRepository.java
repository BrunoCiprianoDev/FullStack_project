package com.bcipriano.minhasfinancas.model.repository;

import com.bcipriano.minhasfinancas.model.entity.Lancamento;
import com.bcipriano.minhasfinancas.model.entity.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = "select sum(l.valor) from Lancamento l join l.usuario u"
            +" where u.id = :idUsuario and l.tipo = :tipo group by u")
    BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario")Long usuario, @Param("tipo") TipoLancamento tipo);

    Optional<Lancamento> findById(Long id);

}
