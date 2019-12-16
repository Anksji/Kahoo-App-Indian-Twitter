package com.kaho.app.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kaho.app.Data.Models.KahooPostModel;
import com.kaho.app.R;
import com.kaho.app.Session.SessionPrefManager;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.DobaraKahooList;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.LikedKahooList;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.MyKahooListFrag;
import com.kaho.app.UI.KahooUi.ViewingKahoo.Profile.ReplyKahooListFrag;
import com.kaho.app.UI.KahooUi.ViewingKahoo.SingleDetailKahooView;
import com.kaho.app.UI.KahooUi.ViewingKahoo.SingleHashTagKahooView;
import com.kaho.app.UI.KahooUi.ViewingKahoo.ViewKahooFrontFragment;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooDobaraViewHolder;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooLinkDobaraVH;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooLinkTextVH;
import com.kaho.app.ViewHolders.KahooViewHolders.KahooViewHolder;
import com.kaho.app.databinding.KahooDobaraSingleLayoutBinding;
import com.kaho.app.databinding.KahooSingleLayoutViewBinding;

import java.util.List;

public class KahooListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private int DOBARA_LINK_KAHOO=4;
    private int LINK_TEXT_KAHOO=5;
    private int DOBARA_REP_TYPE_KAHOO=2;
    private int NEW_TYPE_KAHOO=1;
    private int INBETWEEN_ADS_KAHOO=3;

    private List<KahooPostModel> _itemList;
    private Fragment mContext;
    private boolean isFromViewHolder=false;
    private SessionPrefManager sessionPrefManager;


    public KahooListAdapter(Fragment context,boolean isFromViewHolder,List<KahooPostModel> list){
        this.mContext = context;
        this.isFromViewHolder=isFromViewHolder;
        this._itemList=list;
        sessionPrefManager=new SessionPrefManager(context.getActivity());
    }

    public void updateKahooList(List<KahooPostModel> list){
        this._itemList=list;
    }
    public List<KahooPostModel> getItemList(){
        return _itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder vh;

        if (i==NEW_TYPE_KAHOO){
            return new KahooViewHolder(KahooSingleLayoutViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                    viewGroup, false));
        }else if(i==LINK_TEXT_KAHOO){
            return new KahooLinkTextVH(KahooSingleLayoutViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                    viewGroup, false));
        }else if (i==DOBARA_LINK_KAHOO){
            return new KahooLinkDobaraVH(KahooDobaraSingleLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                    viewGroup, false));
        } else if (i==DOBARA_REP_TYPE_KAHOO){
            return new KahooDobaraViewHolder(KahooDobaraSingleLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                    viewGroup, false));
        }else {
            return new KahooDobaraViewHolder(KahooDobaraSingleLayoutBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                    viewGroup, false));
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rholder, final int i) {

        if (rholder instanceof KahooDobaraViewHolder){
            KahooDobaraViewHolder holder;
            holder = (KahooDobaraViewHolder) rholder;
            holder.setUpKahooView(holder, mContext, _itemList.get(i),i,isFromViewHolder);

            holder.kahooDobaraBinding.moreOptionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.kahooDobaraBinding.moreOptionBtn.setRotation(180);
                    showPopupMenu(v,i,holder.itemView);
                }
            });
        }else if (rholder instanceof KahooLinkDobaraVH){
            KahooLinkDobaraVH holder;
            holder = (KahooLinkDobaraVH) rholder;
            holder.setUpKahooView(holder, mContext, _itemList.get(i),i,isFromViewHolder);

            holder.kahooDobaraBinding.moreOptionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.kahooDobaraBinding.moreOptionBtn.setRotation(180);
                    showPopupMenu(v,i,holder.itemView);
                }
            });
        }else if (rholder instanceof KahooLinkTextVH) {
            KahooLinkTextVH holder;
            holder = (KahooLinkTextVH) rholder;
            holder.setUpKahooView(holder, mContext, _itemList.get(i),i,isFromViewHolder);

            holder.kahooSingleLayoutViewBinding.moreOptionBtn
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.kahooSingleLayoutViewBinding.moreOptionBtn.setRotation(180);
                    showPopupMenu(v,i,holder.itemView);
                }
            });
        }
        else if (rholder instanceof KahooViewHolder) {
            KahooViewHolder holder;
            holder = (KahooViewHolder) rholder;
            holder.setUpKahooView(holder, mContext, _itemList.get(i),i,isFromViewHolder);

            holder.kahooSingleLayoutViewBinding.moreOptionBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    holder.kahooSingleLayoutViewBinding.moreOptionBtn.setRotation(180);
                    showPopupMenu(v,i,holder.itemView);
                }
            });
        }
    }


    private void showPopupMenu(View view, int position,View parentView) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();

        if (_itemList.get(position).getKaho_added_user_id().equalsIgnoreCase(sessionPrefManager.getUserID())){
            inflater.inflate(R.menu.kahoo_menu, popup.getMenu());
        }else {
            inflater.inflate(R.menu.kahoo_othr_user_menu,popup.getMenu());
        }

        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position,parentView));
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                view.setRotation(0);
            }
        });
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        private View parentView;
        public MyMenuItemClickListener(int positon,View parentView) {
            this.position=positon;
            this.parentView=parentView;
        }

        @Override
        public boolean onMenuItemClick(final MenuItem menuItem) {
            Activity activity=mContext.getActivity();
            switch (menuItem.getItemId()) {

                case R.id.rekahoo:
                    if (mContext instanceof ViewKahooFrontFragment){
                        ((ViewKahooFrontFragment) mContext).dobaraKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof SingleDetailKahooView){
                        ((SingleDetailKahooView) mContext).dobaraKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof SingleHashTagKahooView){
                        ((SingleHashTagKahooView) mContext).dobaraKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof DobaraKahooList){
                        ((DobaraKahooList) mContext).dobaraKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof LikedKahooList){
                        ((LikedKahooList) mContext).dobaraKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof MyKahooListFrag){
                        ((MyKahooListFrag) mContext).dobaraKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof ReplyKahooListFrag){
                        ((ReplyKahooListFrag) mContext).dobaraKahooClicked(_itemList.get(position),position);
                    }

                    break;
                case R.id.report:
                    if (mContext instanceof ViewKahooFrontFragment){
                        ((ViewKahooFrontFragment) mContext).reportKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof SingleDetailKahooView){
                        ((SingleDetailKahooView) mContext).reportKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof SingleHashTagKahooView){
                        ((SingleHashTagKahooView) mContext).reportKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof DobaraKahooList){
                        ((DobaraKahooList) mContext).reportKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof LikedKahooList){
                        ((LikedKahooList) mContext).reportKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof MyKahooListFrag){
                        ((MyKahooListFrag) mContext).reportKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof ReplyKahooListFrag){
                        ((ReplyKahooListFrag) mContext).reportKahoo(_itemList.get(position),position);
                    }
                    break;
                case R.id.reply:

                    if (mContext instanceof ViewKahooFrontFragment){
                        ((ViewKahooFrontFragment) mContext).replyKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof SingleDetailKahooView){
                        ((SingleDetailKahooView) mContext).replyKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof SingleHashTagKahooView){
                        ((SingleHashTagKahooView) mContext).replyKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof DobaraKahooList){
                        ((DobaraKahooList) mContext).replyKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof LikedKahooList){
                        ((LikedKahooList) mContext).replyKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof MyKahooListFrag){
                        ((MyKahooListFrag) mContext).replyKahooClicked(_itemList.get(position),position);
                    }else if (mContext instanceof ReplyKahooListFrag){
                        ((ReplyKahooListFrag) mContext).replyKahooClicked(_itemList.get(position),position);
                    }
                    break;
                case R.id.share:

                    if (mContext instanceof ViewKahooFrontFragment){
                        ((ViewKahooFrontFragment) mContext).shareKahooClicked(_itemList.get(position),position,parentView);
                    }else if (mContext instanceof SingleDetailKahooView){
                        ((SingleDetailKahooView) mContext).shareKahooClicked(_itemList.get(position),position,parentView);
                    }else if (mContext instanceof SingleHashTagKahooView){
                        ((SingleHashTagKahooView) mContext).shareKahooClicked(_itemList.get(position),position,parentView);
                    }else if (mContext instanceof DobaraKahooList){
                        ((DobaraKahooList) mContext).shareKahooClicked(_itemList.get(position),position,parentView);
                    }else if (mContext instanceof LikedKahooList){
                        ((LikedKahooList) mContext).shareKahooClicked(_itemList.get(position),position,parentView);
                    }else if (mContext instanceof MyKahooListFrag){
                        ((MyKahooListFrag) mContext).shareKahooClicked(_itemList.get(position),position,parentView);
                    }else if (mContext instanceof ReplyKahooListFrag){
                        ((ReplyKahooListFrag) mContext).shareKahooClicked(_itemList.get(position),position,parentView);
                    }
                    break;

                case R.id.edit:
                    /*if (mContext instanceof ViewKahooFrontFragment) {
                        ((ViewKahooFrontFragment)(mContext)).editKahoo(_itemList.get(position),position);
                    }*/
                    if (mContext instanceof ViewKahooFrontFragment){
                        ((ViewKahooFrontFragment) mContext).editKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof SingleDetailKahooView){
                        ((SingleDetailKahooView) mContext).editKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof SingleHashTagKahooView){
                        ((SingleHashTagKahooView) mContext).editKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof DobaraKahooList){
                        ((DobaraKahooList) mContext).editKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof LikedKahooList){
                        ((LikedKahooList) mContext).editKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof MyKahooListFrag){
                        ((MyKahooListFrag) mContext).editKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof ReplyKahooListFrag){
                        ((ReplyKahooListFrag) mContext).editKahoo(_itemList.get(position),position);
                    }
                    break;

                case R.id.delete:

                    if (mContext instanceof ViewKahooFrontFragment){
                        ((ViewKahooFrontFragment) mContext).deleteKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof SingleDetailKahooView){
                        ((SingleDetailKahooView) mContext).deleteKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof SingleHashTagKahooView){
                        ((SingleHashTagKahooView) mContext).deleteKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof DobaraKahooList){
                        ((DobaraKahooList) mContext).deleteKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof LikedKahooList){
                        ((LikedKahooList) mContext).deleteKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof MyKahooListFrag){
                        ((MyKahooListFrag) mContext).deleteKahoo(_itemList.get(position),position);
                    }else if (mContext instanceof ReplyKahooListFrag){
                        ((ReplyKahooListFrag) mContext).deleteKahoo(_itemList.get(position),position);
                    }

                    break;
                default:
            }
            return false;
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        if (_itemList.get(position).isHas_dobara_kaho()||
                _itemList.get(position).isHas_reply_kaho()){
            //item
            if (_itemList.get(position).getKahoo_text_content().contains("http")||
                    _itemList.get(position).getKahoo_text_content().contains("ftp")){
                return DOBARA_LINK_KAHOO;
            }else {
                return DOBARA_REP_TYPE_KAHOO;
            }
        }else if (_itemList.get(position).isAdsType()){
            return INBETWEEN_ADS_KAHOO;
        }else {
            if (_itemList.get(position).getKahoo_text_content().contains("http")||
                    _itemList.get(position).getKahoo_text_content().contains("ftp")){
                return LINK_TEXT_KAHOO;
            }else {
                return NEW_TYPE_KAHOO;
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != _itemList ? _itemList.size() : 0);
    }



    public void updateKahoAfterLike(String userId, int position) {
        if (_itemList.get(position).getLikedUserIdsList().contains(userId)){
            _itemList.get(position).getLikedUserIdsList().remove(userId);
            Log.e("djfskfssdf","removing from list position "+position);
        }else {
            _itemList.get(position).getLikedUserIdsList().add(userId);
            Log.e("djfskfssdf","adding to list position "+position);
        }
        notifyItemChanged(position,_itemList.get(position));

    }
}
