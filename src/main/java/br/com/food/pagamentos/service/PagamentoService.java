package br.com.food.pagamentos.service;

import br.com.food.pagamentos.dto.PagamentoDto;
import br.com.food.pagamentos.http.PedidoClient;
import br.com.food.pagamentos.model.Pagamento;
import br.com.food.pagamentos.model.Status;
import br.com.food.pagamentos.repository.PagamentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class PagamentoService {

    private PagamentoRepository repository;
    private ModelMapper modelMapper;

    private PedidoClient pedido;
    public PagamentoService(PagamentoRepository repository, ModelMapper modelMapper, PedidoClient pedido) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.pedido = pedido;
    }

    public Page<PagamentoDto> obterTodos(Pageable paginacao){
        return repository.findAll(paginacao)
                .map(p -> modelMapper.map(p,PagamentoDto.class));
    }

    public PagamentoDto obterPorId(Long id){
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        return modelMapper.map(pagamento,PagamentoDto.class);
    }

    public PagamentoDto criarPagamento(PagamentoDto dto){

        Pagamento pagamento = modelMapper.map(dto,Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        repository.save(pagamento);

        return modelMapper.map(pagamento,PagamentoDto.class);
    }

    public PagamentoDto atualizarPagamento(Long Id,PagamentoDto dto){

        Pagamento pagamento = modelMapper.map(dto,Pagamento.class);
        pagamento.setId(Id);
        pagamento = repository.save(pagamento);

        return modelMapper.map(pagamento,PagamentoDto.class);
    }

    public void excluirPagamento(Long id){
        repository.deleteById(id);
    }

    public void confirmaPagamento(Long id){
        Optional<Pagamento> pagamento = repository.findById(id);

        if(!pagamento.isPresent()){
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        repository.save(pagamento.get());
        pedido.atualizaPagamento(pagamento.get().getPedidoId());
    }
}
