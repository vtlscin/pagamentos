package br.com.food.pagamentos.controller;

import br.com.food.pagamentos.dto.PagamentoDto;
import br.com.food.pagamentos.service.PagamentoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PagamentoDto> listar(@PageableDefault(size = 10)Pageable paginacao){
        return service.obterTodos(paginacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDto> detalhar(@PathVariable @NotNull Long id){
        PagamentoDto dto = service.obterPorId(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PagamentoDto> cadastrar(@RequestBody @Valid PagamentoDto dto, UriComponentsBuilder uriBuilder){
        PagamentoDto pagamento = service.criarPagamento(dto);
        URI endereco = uriBuilder.path("/pagamento/{id}").buildAndExpand(pagamento.getId()).toUri();

        return ResponseEntity.created(endereco).body(pagamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDto> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDto dto){
        PagamentoDto pagamentoDto = service.atualizarPagamento(id, dto);
        return ResponseEntity.ok(pagamentoDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoDto> deletar(@PathVariable @NotNull Long id){
        service.excluirPagamento(id);
        return ResponseEntity.noContent().build();
    }
}
