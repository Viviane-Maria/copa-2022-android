package me.dio.copa.catar.features

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dio.copa.catar.core.BaseViewModel
import me.dio.copa.catar.domain.model.Match
import me.dio.copa.catar.domain.model.MatchDomain
import me.dio.copa.catar.domain.usecase.DisableNotificationUseCase
import me.dio.copa.catar.domain.usecase.EnableNotificationUseCase
import me.dio.copa.catar.domain.usecase.GetMatchesUseCase
import me.dio.copa.catar.remote.NotFoundException
import me.dio.copa.catar.remote.UnexpectedException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMatchesUseCase: GetMatchesUseCase,
    private val disableNotificationUseCase: DisableNotificationUseCase,
    private val enableNotificationUseCase: EnableNotificationUseCase,
) : BaseViewModel<MainUiState,MainUiAction>(MainUiState()) {

    init {
        fetchMatches()
    }

    private fun fetchMatches() = viewModelScope.launch {
        //dentro do parênteses chama o invoke, como foi usado com operator não precisa chamá-lo (.invoke) aqui novamente
        getMatchesUseCase()
            .flowOn(Dispatchers.Main) // Dispatchers = threads
            .catch {
                when(it) {
                    is NotFoundException -> sendAction(MainUiAction.MatchesNotFound(it.message ?: "Erro sem mensagem"))
                    is UnexpectedException -> sendAction(MainUiAction.Unexpected)
                }
            }.collect{matches ->
                setState {
                    copy(matches = matches)
                }
            }
    }
    //Muda o estado da notificação, ativa e desativa (ex.: like)
    fun toggleNotification(match: Match) {
        viewModelScope.launch {
            //dentro do runCatching porque caso dê alguma falha o app não fechará sozinho
            runCatching {
                withContext(Dispatchers.Main) {
                    val action = if (match.notificationEnabled) {
                        disableNotificationUseCase(match.id)
                        MainUiAction.DisableNotification(match)
                    } else {
                        enableNotificationUseCase(match.id)
                        MainUiAction.EnableNotification(match)
                    }

                    sendAction(action)
                }
            }
        }

    }
}

//Estado inicial da lista é vazia
data class MainUiState(
    val matches: List<MatchDomain> = emptyList()
)

//Ao trocar de data para interface retira os parênteses porque não tem propriedades
sealed interface MainUiAction{
    //Quando retorna algo que não é esperado
    object Unexpected: MainUiAction
    data class MatchesNotFound(val message: String) : MainUiAction

    data class EnableNotification(val match: MatchDomain) : MainUiAction
    data class DisableNotification(val match: MatchDomain) : MainUiAction
}