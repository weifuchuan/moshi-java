import { Button, Form, Input } from "antd";
import { FormComponentProps } from "antd/lib/form";
import React from "react";

class RolePermissionForm extends React.Component<FormComponentProps & {
    rolePermission: RolePermission
}> {

  handleSubmit = (e: React.FormEvent<any>) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        
      }
    });
  };

  render() {
    const { rolePermission } = this.props;
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
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("roleId", {
            initialValue: rolePermission.roleId,
            rules: []
          })(<Input />)}
        </Form.Item>
        <Form.Item {...formItemLayout} label="">
          {getFieldDecorator("permissionId", {
            initialValue: rolePermission.permissionId,
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

export default Form.create()(RolePermissionForm);