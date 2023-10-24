package me.dio.copa.catar.features


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.dio.copa.catar.domain.model.MatchDomain
import me.dio.copa.catar.ui.theme.Shapes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import me.dio.copa.catar.R
import me.dio.copa.catar.domain.extensions.getDate
import me.dio.copa.catar.domain.model.TeamDomain

typealias NotificationOnClick = (match: MatchDomain) -> Unit

@Composable
fun MainScreen(matches: List<MatchDomain>, OnNotificationClick: NotificationOnClick) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        //escopo da lista
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(matches) { match ->
                MatchInfo(match, OnNotificationClick)

            }
        }
    }
}

@Composable
fun MatchInfo(match: MatchDomain, OnNotificationClick: NotificationOnClick) {
    Card(
        shape = Shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            AsyncImage(
                model = match.stadium.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(160.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Notification(match, OnNotificationClick)
                Title(match)
                Teams(match)


            }
        }
    }
}


@Composable
fun Notification(match: MatchDomain, onClick: NotificationOnClick) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        val drawable = if (match.notificationEnabled) R.drawable.ic_notifications_active
        else R.drawable.ic_notifications

        Image(
            painter = painterResource(id = drawable),
            modifier = Modifier.clickable {
                onClick(match)
            },
            contentDescription = null
        )
    }
}

@Composable
fun Title(match: MatchDomain) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${match.date.getDate()} - ${match.name}",
            //copy: copia toda a estrutura e muda apenas o elemento que deseja alterar, no caso abaixo a cor
            style = MaterialTheme.typography.h6.copy(color = Color.White)
        )
    }
}

@Composable
fun Teams(match: MatchDomain) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TeamItem(team = match.team1, false)

        Text(
            text = "X",
            modifier = Modifier.padding(end = 16.dp, start = 16.dp),
            style = MaterialTheme.typography.h6.copy(color = Color.White)
        )

        TeamItem(team = match.team2, true)
    }
}

@Composable
fun TeamItem(team: TeamDomain, inverse:Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Inverte a disposição do segundo time para ficar: flag team x team flag
        val (flagText, nameText) = if (inverse) {
            team.displayName to team.flag
        } else {
            team.flag to team.displayName
        }
        Text(
            text = flagText,
            modifier = Modifier.align(Alignment.CenterVertically),
            //Porém quando inverte, o nome fica grande e a bandeira fica pequena, não mantém o style dos rescpectivos Texts
            //Então, uso o if else para inverter o tamanho também e assim corrigir
            style = if (inverse) {
                MaterialTheme.typography.h6.copy(color = Color.White)
            } else {
                MaterialTheme.typography.h3.copy(color = Color.White)
            }
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = nameText,
            textAlign = TextAlign.Center,
            style = if (inverse) {
                MaterialTheme.typography.h3.copy(color = Color.White)
            } else {
                MaterialTheme.typography.h6.copy(color = Color.White)
            }
        )
    }
}