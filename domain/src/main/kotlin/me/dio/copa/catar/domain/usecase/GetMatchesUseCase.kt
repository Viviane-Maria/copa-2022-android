package me.dio.copa.catar.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.dio.copa.catar.domain.model.Match
import me.dio.copa.catar.domain.repositories.MatchesRepository
import javax.inject.Inject

//Responsabilidade: Retorna a lista de matches (partidas) do repository
class GetMatchesUseCase @Inject constructor(
    private val repository: MatchesRepository
) {
    //suspend function (chamada assíncrona e suspende a execução)
    //flow (retornar mais de um resultado, se atualiza não precisa chamar novamente)
    suspend operator fun invoke(): Flow<List<Match>> {
        return repository.getMatches()
    }
}