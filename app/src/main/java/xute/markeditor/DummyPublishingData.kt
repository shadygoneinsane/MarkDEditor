package xute.markeditor

import xute.markdeditor.datatype.DraftDataItemModel
import xute.markdeditor.models.DraftModel
import xute.markdeditor.styles.TextComponentStyle
import xute.markdeditor.styles.TextModeType
import java.util.*

/**
 * File Description
 * Created by: Vikesh Dass
 * Created on: 06-06-2020
 */
object DummyPublishingData {
    @JvmStatic
    fun getDraftContent(): DraftModel {
        val contentTypes = ArrayList<DraftDataItemModel>()
        val title = DraftDataItemModel()
        title.itemType = DraftModel.ITEM_TYPE_TEXT
        title.content = "IN CONVERSATION WITH NITINN R MIRANNI"
        title.mode = TextModeType.MODE_PLAIN
        title.style = TextComponentStyle.HEADING_H1

        val parah1 = DraftDataItemModel()
        parah1.itemType = DraftModel.ITEM_TYPE_TEXT
        parah1.content = "Nitinn R Miranni is a world renowned Indian comedian/entertainer who was awarded The “Comedian of the Year” 2017-2018 at the “Esquire Middle East Man At His Best Awards” held in Dubai alongside making it to the Masala Magazine’s “U.A.E’s HOT 100 People’s List” 2017-2018."
        parah1.mode = TextModeType.MODE_PLAIN
        parah1.style = TextComponentStyle.FORMAT_NORMAL

        val heading2 = DraftDataItemModel()
        heading2.itemType = DraftModel.ITEM_TYPE_TEXT
        heading2.content = "Early Life"
        heading2.mode = TextModeType.MODE_PLAIN
        heading2.style = TextComponentStyle.HEADING_H2

        val parah2 = DraftDataItemModel()
        parah2.itemType = DraftModel.ITEM_TYPE_TEXT
        parah2.content = "Miranni recollects that in school he felt like the most miserable person in the world. He was not at all a good student and once a friend had sent him his photograph while in school where the one kid who looked the most unhappy was none other than him. He further adds that’s he was a different child in school."
        parah2.mode = TextModeType.MODE_PLAIN
        parah2.style = TextComponentStyle.FORMAT_NORMAL

        val bl = DraftDataItemModel()
        bl.itemType = DraftModel.ITEM_TYPE_TEXT
        bl.content = "He thought of things differently, "
        bl.mode = TextModeType.MODE_PLAIN
        bl.style = TextComponentStyle.FORMAT_NORMAL

        val b2 = DraftDataItemModel()
        b2.itemType = DraftModel.ITEM_TYPE_TEXT
        b2.content = "“ek alag tarika tha”"
        b2.mode = TextModeType.MODE_PLAIN
        b2.style = TextComponentStyle.QUOTE_ITALIC

        val b3 = DraftDataItemModel()
        b3.itemType = DraftModel.ITEM_TYPE_TEXT
        b3.content = "as he puts it and hence the price that he often paid for being different was that he was left out."
        b3.mode = TextModeType.MODE_PLAIN
        b3.style = TextComponentStyle.FORMAT_NORMAL

        val hrType = DraftDataItemModel()
        hrType.itemType = DraftModel.ITEM_TYPE_HR

        val imageType = DraftDataItemModel()
        imageType.itemType = DraftModel.ITEM_TYPE_IMAGE
        imageType.downloadUrl = "https://images.indianexpress.com/2015/09/nitin-mirani-759.jpg"
        imageType.caption = "Nitin Mirani"

        contentTypes.add(title)
        contentTypes.add(parah1)
        contentTypes.add(heading2)
        contentTypes.add(parah2)
        contentTypes.add(imageType)
        contentTypes.add(bl)
        contentTypes.add(b2)
        contentTypes.add(b3)
        return DraftModel(contentTypes)
    }

    private fun getDraftContent2(): DraftModel {
        val contentTypes = ArrayList<DraftDataItemModel>()
        val heading = DraftDataItemModel()
        heading.itemType = DraftModel.ITEM_TYPE_TEXT
        heading.content = "Kajal Aggarwal filmography"
        heading.mode = TextModeType.MODE_PLAIN
        heading.style = TextComponentStyle.HEADING_H1
        val sub_heading = DraftDataItemModel()
        sub_heading.itemType = DraftModel.ITEM_TYPE_TEXT
        sub_heading.content = "Nominated"
        sub_heading.mode = TextModeType.MODE_PLAIN
        sub_heading.style = TextComponentStyle.HEADING_H3
        val bl = DraftDataItemModel()
        bl.itemType = DraftModel.ITEM_TYPE_TEXT
        bl.content = "A super star of south movies!"
        bl.mode = TextModeType.MODE_PLAIN
        bl.style = TextComponentStyle.QUOTE_ITALIC
        val body = DraftDataItemModel()
        body.itemType = DraftModel.ITEM_TYPE_TEXT
        body.content = """
            
            Kajal Aggarwal in March 2017
            Kajal Aggarwal is an Indian actress who appears in primarily in Tamil and Telugu films.[1] She made her acting debut with a minor role in the Hindi film Kyun! Ho Gaya Na..., a box office failure. She later signed up P. Bharathiraja's Tamil film Bommalattam, which was to have been her first film in that language, but it was delayed.
            """.trimIndent()
        body.mode = TextModeType.MODE_PLAIN
        body.style = TextComponentStyle.FORMAT_NORMAL
        val hrType = DraftDataItemModel()
        hrType.itemType = DraftModel.ITEM_TYPE_HR
        val imageType = DraftDataItemModel()
        imageType.itemType = DraftModel.ITEM_TYPE_IMAGE
        imageType.downloadUrl = "https://cdn.shopify.com/s/files/1/0166/3704/products/78008-3_grande.jpg"
        imageType.caption = "Cute Pink Photo"
        val filmsList1 = DraftDataItemModel()
        filmsList1.itemType = DraftModel.ITEM_TYPE_TEXT
        filmsList1.style = TextComponentStyle.FORMAT_NORMAL
        filmsList1.mode = TextModeType.MODE_OL
        filmsList1.content = "2009 – Filmfare Award for Best Actress – Telugu for Magadheera"
        val filmsList2 = DraftDataItemModel()
        filmsList2.itemType = DraftModel.ITEM_TYPE_TEXT
        filmsList2.style = TextComponentStyle.FORMAT_NORMAL
        filmsList2.mode = TextModeType.MODE_OL
        filmsList2.content = "2010 – Filmfare Award for Best Actress – Telugu for Darling"
        contentTypes.add(heading)
        contentTypes.add(filmsList1)
        contentTypes.add(imageType)
        //    contentTypes.add(filmsList2);
//    contentTypes.add(filmsList2);
//    contentTypes.add(filmsList2);
//    contentTypes.add(imageType);
//    contentTypes.add(imageType);
//    contentTypes.add(filmsList2);
//    contentTypes.add(filmsList2);
        return DraftModel(contentTypes)
    }
}