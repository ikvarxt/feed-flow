package com.prof18.feedflow.home.components

import FeedFlowTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.prof18.feedflow.MR
import com.prof18.feedflow.ui.preview.FeedFlowPreview
import com.prof18.feedflow.ui.theme.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun NoFeedsSourceView(
    modifier: Modifier = Modifier,
    onAddFeedClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(resource = MR.strings.no_feeds_found_message),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            modifier = Modifier
                .padding(top = Spacing.regular),
            onClick = {
                onAddFeedClick()
            },
        ) {
            Text(stringResource(resource = MR.strings.add_feed))
        }
    }
}

@FeedFlowPreview
@Composable
fun NoFeedsViewPreview() {
    FeedFlowTheme {
        Surface {
            NoFeedsSourceView {}
        }
    }
}
