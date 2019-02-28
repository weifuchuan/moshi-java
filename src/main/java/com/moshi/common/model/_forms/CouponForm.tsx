import { Button, Form, Input } from "antd";
import { FormComponentProps } from "antd/lib/form";
import React from "react";

class CouponForm extends React.Component<FormComponentProps & {
    coupon: Coupon
}> {

  handleSubmit = (e: React.FormEvent<any>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        
      }
    });
  };

  render() {
    const { coupon } = this.props;
    const { getFieldDecorator } = this.props.form;

    const formItemLayout = {
      labelCol: {
        xs: { span: 24 }
      },
      wrapperCol: {
        xs: { span: 24 }
      }
    };
    const tailFormItemLayout = {
      wrapperCol: {
        xs: {
          span: 24,
          offset: 0
        }
      }
    };

    return (
      <Form onSubmit={this.handleSubmit}>
        <Form.Item {...formItemLayout} label="id">
          {getFieldDecorator("id", {
            initialValue: coupon.id,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="价值">
          {getFieldDecorator("value", {
            initialValue: coupon.value,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="折扣或金额券">
          {getFieldDecorator("valueType", {
            initialValue: coupon.valueType,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="所属人">
          {getFieldDecorator("accountId", {
            initialValue: coupon.accountId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="可用于实体，null则通用">
          {getFieldDecorator("refId", {
            initialValue: coupon.refId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="可用于的实体的类型，course、通用">
          {getFieldDecorator("refType", {
            initialValue: coupon.refType,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="创建时间">
          {getFieldDecorator("createAt", {
            initialValue: coupon.createAt,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="过期时间">
          {getFieldDecorator("offerTo", {
            initialValue: coupon.offerTo,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="状态">
          {getFieldDecorator("status", {
            initialValue: coupon.status,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...tailFormItemLayout}>
          <Button type="primary" htmlType="submit">
            确定
          </Button>
        </Form.Item>
      </Form>
    );
  }
}

export default Form.create()(CouponForm);