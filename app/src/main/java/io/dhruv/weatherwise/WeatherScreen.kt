package io.dhruv.weatherwise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.text.SimpleDateFormat
import java.util.Date


//@Preview(
//    showBackground = true,
//    device = "spec:width=370dp,height=830dp,isRound=false,chinSize=0dp,orientation=portrait",
//    backgroundColor = 0xFF111111
//)
@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }
    val post by viewModel.posts.observeAsState()


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFE7E75),
                        Color(0xFFC7BEB8)
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {

        Column {
            Text(
                text = post?.dt?.let {
                    val dt = Date(it)
                    val sdf = SimpleDateFormat("h:mm aa")
                    val time1 = sdf.format(dt)
                    time1
                }?.toString() ?: " ",
                fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                color = Color.White
            )
            Text(
                text = post?.name ?: " ", color = Color.White,
                fontFamily = FontFamily(Font(R.font.montserrat_medium)),
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    textWidth = with(density) {
                        coordinates.size.width.toDp()
                    }
                },
                fontSize = 44.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0x80FFFFFF), modifier = Modifier.width(textWidth))
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "Today ",
                    color = Color(0xCCFFFFFF),
                    fontFamily = FontFamily(Font(R.font.montserrat_medium))
                )
                Text(
                    text = post?.dt?.let {
                        val dt = Date(it)
                        val sdf = SimpleDateFormat("dd-MM-yy")
                        val time1 = sdf.format(dt)
                        time1
                    }?.toString() ?: " ",
                    color = Color.White,
                    fontWeight = FontWeight(600),
                    fontFamily = FontFamily(Font(R.font.montserrat_medium))
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = post?.main?.temp?.let {
                        val time = it.minus(273.15)
                        time.toInt()
                    }.toString().plus("°C") ?: " ",
                    color = Color.White,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.montserrat_medium))

                )
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/10n@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                )
            }
            rightInfo(post)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .clip(shape = RoundedCornerShape(20.dp))
                            .background(color = Color(0x80FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.snowflake),
                            contentDescription = "",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Text(
                            text = " Max temp",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium))
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = post?.main?.temp_max.let {
                            val time = it?.minus(273.15) ?: 273.15
                            time.toInt()
                        }?.toString().plus("°C") ?: " ",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium))
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .clip(shape = RoundedCornerShape(20.dp))
                            .background(color = Color(0x80FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sun),
                            contentDescription = "",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Text(
                            text = " Min temp",
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium))
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = post?.main?.temp_min.let {
                            val time = it?.minus(273) ?: 273
                            time.toInt()
                        }?.toString().plus("°C") ?: " ",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium))
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Details:",
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp),
                fontFamily = FontFamily(Font(R.font.montserrat_medium))
            )
            Divider(color = Color(0x80FFFFFF))



            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sunrise",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium))
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_upward),
                            contentDescription = "",
                            tint = Color(0xFFfcb972),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = post?.sys?.sunrise?.let {
                            val dt = Date(it)
                            val sdf = SimpleDateFormat("h:mm aa")
                            val time1 = sdf.format(dt)
                            time1
                        } ?: " ",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium))
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_upward),
                            contentDescription = "",
                            tint = Color(0xFFB9DBFF),
                            modifier = Modifier
                                .rotate(180f)
                                .size(36.dp)
                        )
                        Text(
                            text = "Sunset",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_medium))
                        )
                    }
                    Text(
                        text = post?.sys?.sunset?.let {
                            val dt = Date(it)
                            val sdf = SimpleDateFormat("h:mm aa")
                            val time1 = sdf.format(dt)
                            time1
                        } ?: " ",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium))
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.sunset),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth()
            )

        }

    }
}

@Composable
fun rightInfo(post: ResponseModal?) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = post?.weather?.takeIf { it.isNotEmpty() }?.get(0)?.description.let {
                val wind = it?.split(" ")
                    ?.map { it.replaceFirstChar { it.uppercase() } }
                    ?.joinToString("\n") { it }
                wind
            } ?: " ",
            fontSize = 48.sp,
            color = Color.White,
            lineHeight = TextUnit(1F, type = TextUnitType.Em),
            fontFamily = FontFamily(Font(R.font.montserrat_medium))
        )

        Column(horizontalAlignment = Alignment.End) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color(0x80FFFFFF))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.wind_sign),
                    contentDescription = "",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Text(
                    text = " Wind",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.montserrat_medium))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post?.wind?.speed?.toString().plus("m/s") ?: " ",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.montserrat_medium))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color(0x80FFFFFF))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.drop),
                    contentDescription = "",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp),
                    tint = Color.White
                )
                Text(
                    text = "Humidity",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.montserrat_medium))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post?.main?.humidity?.toString().plus("%") ?: " ",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.montserrat_medium))
            )
        }
    }
}