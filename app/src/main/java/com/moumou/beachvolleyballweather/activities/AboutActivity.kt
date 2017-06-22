package com.moumou.beachvolleyballweather.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.context.IconicsLayoutInflater
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import com.moumou.beachvolleyballweather.BuildConfig
import com.moumou.beachvolleyballweather.R

class AboutActivity : MaterialAboutActivity() {
    override fun getActivityTitle() : CharSequence? {
        return getString(R.string.about)
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        LayoutInflaterCompat.setFactory(layoutInflater, IconicsLayoutInflater(delegate))
        super.onCreate(savedInstanceState)
    }

    override fun getMaterialAboutList(p0 : Context) : MaterialAboutList {

        val appCardBuilder = MaterialAboutCard
                .Builder()
                .addItem(MaterialAboutTitleItem.Builder()
                                 .text(R.string.app_name)
                                 .icon(R.mipmap.ic_launcher2)
                                 .build())
                .addItem(MaterialAboutActionItem.Builder()
                                 .text(R.string.version)
                                 .subText(BuildConfig.VERSION_NAME)
                                 .icon(IconicsDrawable(this).icon(
                                         MaterialDesignIconic.Icon.gmi_info_outline))
                                 .build())

        val authorCardBuilder = MaterialAboutCard
                .Builder()
                .title(R.string.author)
                .addItem(MaterialAboutActionItem.Builder()
                                 .text(R.string.dev_name)
                                 .icon(IconicsDrawable(this).icon(
                                         MaterialDesignIconic.Icon.gmi_account))
                                 .setOnClickAction {
                                     //TODO link to google play account
                                 }
                                 .build())
                .addItem(MaterialAboutActionItem.Builder()
                                 .text(R.string.github)
                                 .subText(R.string.github_username)
                                 .icon(
                                         IconicsDrawable(this).icon(
                                                 MaterialDesignIconic.Icon.gmi_github))
                                 .setOnClickAction {
                                     MaterialAboutItemOnClickAction {
                                         val browserIntent = Intent(Intent.ACTION_VIEW,
                                                                    Uri.parse(
                                                                            getString(
                                                                                    R.string.github_link)))
                                         startActivity(browserIntent)
                                     }
                                 }.build())

        val supportCardBuilder = MaterialAboutCard
                .Builder()
                .title(R.string.support_development)
                .addItem(MaterialAboutActionItem.Builder()
                                 .text(R.string.report_bug)
                                 .subText(R.string.report_bug_subtext)
                                 .icon(IconicsDrawable(this).icon(
                                         MaterialDesignIconic.Icon.gmi_bug
                                 ))
                                 .setOnClickAction {
                                     val emailIntent = Intent(Intent.ACTION_SENDTO)
                                     emailIntent.data = (Uri.parse(getString(R.string.mailto) + getString(
                                             R.string.email) + getString(
                                             R.string.mail_body)))

                                     startActivity(Intent.createChooser(emailIntent,
                                                                        getString(R.string.send_mail_action)))
                                 }
                                 .build())
                .addItem(MaterialAboutActionItem.Builder()
                                 .text(R.string.rate_app)
                                 .icon(IconicsDrawable(this).icon(
                                         MaterialDesignIconic.Icon.gmi_star
                                 ))
                                 .setOnClickAction {
                                     //TODO add google play rate action
                                 }
                                 .build())
                .addItem(MaterialAboutActionItem.Builder()
                                 .text(R.string.share_app)
                                 .icon(IconicsDrawable(this).icon(
                                         MaterialDesignIconic.Icon.gmi_share
                                 ))
                                 .setOnClickAction {
                                     val i = Intent(Intent.ACTION_SEND)
                                     i.type = "text/plain"
                                     i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                                     i.putExtra(Intent.EXTRA_TEXT,
                                                getString(R.string.share_app_action_text))
                                     startActivity(Intent.createChooser(i,
                                                                        getString(R.string.share)))
                                 }
                                 .build())

        return MaterialAboutList.Builder()
                .addCard(appCardBuilder.build())
                .addCard(authorCardBuilder.build())
                .addCard(supportCardBuilder.build())
                .build()
    }
}