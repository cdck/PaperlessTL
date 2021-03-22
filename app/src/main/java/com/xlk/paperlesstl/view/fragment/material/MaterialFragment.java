package com.xlk.paperlesstl.view.fragment.material;

import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.xlk.paperlesstl.R;
import com.xlk.paperlesstl.view.base.BaseFragment;
import com.xlk.paperlesstl.view.fragment.material.node.FileNodeAdapter;
import com.xlk.paperlesstl.view.fragment.material.node.LevelDirNode;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/3/8.
 * @desc
 */
public class MaterialFragment extends BaseFragment<MaterialPresenter> implements MaterialContract.View {

    private RecyclerView rv_material;
    private FileNodeAdapter fileNodeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meet_material;
    }

    @Override
    protected void initView(View inflate) {
        rv_material = inflate.findViewById(R.id.rv_material);
    }

    @Override
    protected MaterialPresenter initPresenter() {
        return new MaterialPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryDir();
    }

    @Override
    protected void onShow() {
        presenter.queryDir();
    }

    @Override
    public void showFiles() {
        if (fileNodeAdapter == null) {
            if (!presenter.showFiles.isEmpty()) {
                BaseNode baseNode = presenter.showFiles.get(0);
                if (baseNode instanceof LevelDirNode) {
                    LevelDirNode dirNode = (LevelDirNode) baseNode;
                    dirNode.setExpanded(true);
                }
            } else {
                LogUtils.e(TAG, "会议目录和文件是空的");
            }
            fileNodeAdapter = new FileNodeAdapter(presenter.showFiles);
            rv_material.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_material.setAdapter(fileNodeAdapter);
        } else {
            fileNodeAdapter.setList(presenter.showFiles);
            fileNodeAdapter.notifyDataSetChanged();
        }
    }
}
