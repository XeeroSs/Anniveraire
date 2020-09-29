package com.xeross.anniveraire.controller.gallery

import android.os.Bundle
import com.bumptech.glide.Glide
import com.xeross.anniveraire.R
import com.xeross.anniveraire.controller.base.BaseActivity
import com.xeross.anniveraire.utils.Constants
import kotlinx.android.synthetic.main.activity_gallery_detail.*

class GalleryDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(Constants.ID_GALLERY)?.let { s ->
            Glide.with(this).load(s)
                    .thumbnail(0.5f)
                    .useAnimationPool(true)
                    .into(gallery_detail_activity_image)
        } ?: finish()
    }

    override fun getToolBar() = R.id.gallery_detail_activity_toolbar

    override fun getLayoutId() = R.layout.activity_gallery_detail
}