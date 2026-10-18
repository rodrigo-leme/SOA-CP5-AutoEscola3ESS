package br.com.fiap3ess.autoescola3ess.exception;

import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.CepNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarGenericNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Stream<DadosBadRequest>> tratarBadRequest(MethodArgumentNotValidException e) {
        List<FieldError> erros = e.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosBadRequest::new));
    }

    @ExceptionHandler(InstrutorNotFoundException.class)
    public ResponseEntity<DadosErro> tratarInstrutorNotFound(InstrutorNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErro(e.getMessage()));
    }

    @ExceptionHandler(AlunoNotFoundException.class)
    public ResponseEntity<DadosErro> tratarAlunoNotFound(AlunoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErro(e.getMessage()));
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<DadosErro> tratarCepNotFound(CepNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErro(e.getMessage()));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<DadosErro> tratarValidacao(ValidacaoException e) {
        return ResponseEntity.badRequest().body(new DadosErro(e.getMessage()));
    }

    private record DadosBadRequest(String campo, String mensagem) {
        public DadosBadRequest(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

    private record DadosErro(String mensagem) {
    }
}
