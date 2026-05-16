package br.com.fiap.techchallenge.feedbackplatform.infrastructure.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAvaliacaoRequest(
	@NotBlank(message = "Descrição é obrigatória.") 
	@Size(min = 3, max = 2000, message = "Descrição deve ter entre 3 e 2000 caracteres.") 
	String descricao,

	@NotNull(message = "Nota é obrigatória.") 
	@Min(value = 0, message = "Nota deve ser maior ou igual a 0.") 
	@Max(value = 10, message = "Nota deve ser menor ou igual a 10.") Integer nota) {}
